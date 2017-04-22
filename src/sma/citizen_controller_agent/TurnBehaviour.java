package sma.citizen_controller_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Functions;
import sma.model.VoteRequest;
import sma.model.VoteResults;

public class TurnBehaviour extends SimpleBehaviour {
	private final static String STATE_INIT = "INIT";
	private final static String STATE_WAITING_START = "WAITING_START";
	private final static String STATE_END_TURN = "END_TURN";
	private final static String STATE_SEND_WAKE_ALL = "SEND_WAKE_ALL";
	private final static String STATE_RECEIVE_WAKE_ALL = "RECEIVE_WAKE_ALL";
	private final static String STATE_SEND_KILL_VICTIMS_REQUEST = "SEND_KILL_VICTIMS_REQUEST";
	private final static String STATE_RECEIVE_KILL_VICTIMS_REQUEST = "RECEIVE_KILL_VICTIMS_REQUEST";
	private final static String STATE_SEND_CHECK_ENDGAME = "SEND_CHECK_ENDGAME";
	private final static String STATE_RECEIVE_CHECK_ENDGAME = "RECEIVE_CHECK_ENDGAME";
	private final static String STATE_SEND_ELECTION_REQUEST = "SEND_ELECTION_REQUEST";
	private final static String STATE_RECEIVE_ELECTION_REQUEST = "RECEIVE_ELECTION_REQUEST";
	private final static String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final static String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";
	private final static String STATE_SEND_SLEEP_ALL = "SEND_SLEEP_ALL";
	private final static String STATE_RECEIVE_SLEEP_ALL = "RECEIVE_SLEEP_ALL";

	private String step;
	private String nextStep;
	private boolean previousVictims;
	private int cptCitizens;


	private CitizenControllerAgent ctrlAgent;

	public TurnBehaviour(CitizenControllerAgent citizenControllerAgent) {
		super(citizenControllerAgent);

		this.ctrlAgent = citizenControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		System.out.println("STATE = "+this.step);

		/** etat initial **/
		if(this.step.equals(STATE_INIT))
		{
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			previousVictims = false;
			cptCitizens = 0;

			this.nextStep = STATE_WAITING_START;
		}
		/** etat d'attente de début de tour **/
		else if(this.step.equals(STATE_WAITING_START))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("START_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				Functions.updateDayState("DAY", ctrlAgent, ctrlAgent.getGameid());
				this.nextStep = STATE_SEND_WAKE_ALL;
			}
			else
			{
				block();
			}

		}
		/** etat envoi des requêtes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ALL))
		{
			for(AID aid : DFServices.findGameAgent("PLAYER", "CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid()))
			{
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(aid);
				messageRequest.setConversationId("WAKE_PLAYER");
				this.myAgent.send(messageRequest);
			}

			this.nextStep = STATE_RECEIVE_WAKE_ALL;
		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_RECEIVE_WAKE_ALL))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptCitizens;
				if(this.cptCitizens == DFServices.findGameAgent("PLAYER", "CITIZEN", this.myAgent, this.ctrlAgent.getGameid()).size())
				{
					this.cptCitizens = 0;
					this.nextStep = STATE_SEND_KILL_VICTIMS_REQUEST;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_WAKE_ALL;
				}
			}
			else
			{
				block();
			}
		}
		/** etat envoi requete pour tuer les  victimes désignées durant la nuit **/
		else if(this.step.equals(STATE_SEND_KILL_VICTIMS_REQUEST))
		{
			System.err.println("... kill ... "+this.previousVictims);

			this.ctrlAgent.addBehaviour(new KillVictimsBehaviour(this.ctrlAgent));
			this.nextStep = STATE_RECEIVE_KILL_VICTIMS_REQUEST;



		}
		/** etat pour recevoir la confirmation de la mort des victimes **/
		else if(this.step.equals(STATE_RECEIVE_KILL_VICTIMS_REQUEST))
		{
			/** le behaviour kill victims a terminé son travail **/
			if(this.ctrlAgent.isFlag_victims() && this.ctrlAgent.getVictims().isEmpty())
			{
				this.ctrlAgent.setFlag_victims(false);
				this.nextStep = STATE_SEND_CHECK_ENDGAME;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_KILL_VICTIMS_REQUEST;
			}
		}
		/** etat envoi requete pour connaître l'etat de jeu **/
		else if(this.step.equals(STATE_SEND_CHECK_ENDGAME))
		{
			System.err.println("... check ... "+this.previousVictims);
			List<AID> agents = DFServices.findGameAgent("CONTROLLER", "GAME", this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				//System.out.println("=> "+agents.get(0).getLocalName());
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setSender(this.ctrlAgent.getAID());
				message.addReceiver(agents.get(0));
				message.setConversationId("CHECK_END_GAME");
				this.ctrlAgent.send(message);

				this.nextStep = STATE_RECEIVE_CHECK_ENDGAME;
			}
			else
			{
				this.nextStep = STATE_SEND_CHECK_ENDGAME;
			}
		}
		/** etat reception de l'état de jeu **/
		else if(this.step.equals(STATE_RECEIVE_CHECK_ENDGAME))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("CONTINUE_GAME"));

			ACLMessage message = this.myAgent.receive(mt);

			if(message != null)
			{
				System.err.println("... receive ...");
				if(!this.previousVictims)
				{
					this.previousVictims = true;
					this.nextStep = STATE_SEND_ELECTION_REQUEST;
				}
				else
				{
					this.nextStep = STATE_SEND_SLEEP_ALL;
				}
			}
			else
			{
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("END_GAME"));

				message = this.myAgent.receive(mt);

				if(message != null)
				{
					System.err.println("... receive ...");
					this.nextStep = STATE_END_TURN;
				}
				else
				{
					block();
				}
			}
		}
		/** etat envoi de la requete de vote de l'election de maire **/
		else if(this.step.equals(STATE_SEND_ELECTION_REQUEST))
		{

			List<String> choices = new ArrayList<String>();
			for(AID aid : DFServices.findGameAgent("PLAYER", "CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid()))
			{
				choices.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setRequest("ELECTION");
			request.setChoices(choices);
			request.setVoters(choices);

			ObjectMapper mapper = new ObjectMapper();
			String json = "";
			try {
				json = mapper.writeValueAsString(request);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.ctrlAgent.getAID());
			messageRequest.setConversationId("VOTE_REQUEST");
			messageRequest.setContent(json);
			this.ctrlAgent.send(messageRequest);


			this.nextStep = STATE_RECEIVE_ELECTION_REQUEST;
		}

		/** etat reception du resultat de l'election **/
		else if(this.step.equals(STATE_RECEIVE_ELECTION_REQUEST))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
			else
			{
				block();
			}


			//this.nextStep = STATE_SEND_VOTE_REQUEST;
		}
		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			for(AID aid : DFServices.findGameAgent("PLAYER", "CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid()))
			{
				choices.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setRequest("CITIZEN_VOTE");
			request.setChoices(choices);
			request.setVoters(choices);

			ObjectMapper mapper = new ObjectMapper();
			String json = "";
			try {
				json = mapper.writeValueAsString(request);
			} catch (IOException e) {
				e.printStackTrace();
			}


			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.ctrlAgent.getAID());
			messageRequest.setConversationId("VOTE_REQUEST");
			messageRequest.setContent(json);
			this.ctrlAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_VOTE_REQUEST;
		}
		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_REQUEST))
		{

			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String victim = message.getContent();
				AID aidVictim = new AID(victim);
				this.ctrlAgent.getVictims().push(aidVictim);

				this.nextStep = STATE_SEND_KILL_VICTIMS_REQUEST;
			}
			else
			{
				block();
			}

			//this.nextStep = STATE_SEND_KILL_VICTIMS_REQUEST;
		}
		/** etat envoi des requêtes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			for(AID aid : DFServices.findGameAgent("PLAYER", "CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid()))
			{
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(aid);
				messageRequest.setConversationId("SLEEP_PLAYER");
				this.myAgent.send(messageRequest);
			}

			this.nextStep = STATE_RECEIVE_SLEEP_ALL;
		}
		/** reception des confirmations de sommeil **/
		else if(this.step.equals(STATE_RECEIVE_SLEEP_ALL))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptCitizens;
				if(this.cptCitizens == DFServices.findGameAgent("PLAYER", "CITIZEN", this.myAgent, this.ctrlAgent.getGameid()).size())
				{
					this.cptCitizens = 0;
					this.nextStep = STATE_END_TURN;
					
					Functions.updateDayState("NIGHT", ctrlAgent, ctrlAgent.getGameid());
				}
				else
				{
					this.nextStep = STATE_RECEIVE_SLEEP_ALL;
				}
			}
			else
			{
				block();
			}
		}
		/** etat fin de tour **/
		else if(this.step.equals(STATE_END_TURN))
		{
			List<AID> agents = DFServices.findGameAgent("CONTROLLER", "GAME", this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				ACLMessage  message = new ACLMessage(ACLMessage.INFORM);
				message.setConversationId("END_TURN");
				message.setSender(this.ctrlAgent.getAID());
				message.addReceiver(agents.get(0));
				this.ctrlAgent.send(message);
			}
			this.nextStep = STATE_INIT;
		}


		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return false;
	}

}
