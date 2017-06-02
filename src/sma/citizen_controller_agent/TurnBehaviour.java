package sma.citizen_controller_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Functions;
import sma.model.Roles;
import sma.model.Status;
import sma.model.VoteRequest;

/***
 * Behaviour de gestion du tour des citizens
 * @author Davy
 *
 */
public class TurnBehaviour extends SimpleBehaviour {
	private final String STATE_INIT = "INIT";
	private final String STATE_WAITING_START = "WAITING_START";
	private final String STATE_END_TURN = "END_TURN";
	private final String STATE_SEND_WAKE_ALL = "SEND_WAKE_ALL";
	private final String STATE_RECEIVE_WAKE_ALL = "RECEIVE_WAKE_ALL";
	private final String STATE_SEND_KILL_VICTIMS_REQUEST = "SEND_KILL_VICTIMS_REQUEST";
	private final String STATE_RECEIVE_KILL_VICTIMS_REQUEST = "RECEIVE_KILL_VICTIMS_REQUEST";
	private final String STATE_SEND_CHECK_ENDGAME = "SEND_CHECK_ENDGAME";
	private final String STATE_RECEIVE_CHECK_ENDGAME = "RECEIVE_CHECK_ENDGAME";
	private final String STATE_SEND_ELECTION_REQUEST = "SEND_ELECTION_REQUEST";
	private final String STATE_RECEIVE_ELECTION_REQUEST = "RECEIVE_ELECTION_REQUEST";
	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";
	private final String STATE_SEND_SLEEP_ALL = "SEND_SLEEP_ALL";
	private final String STATE_RECEIVE_SLEEP_ALL = "RECEIVE_SLEEP_ALL";


	private final String STATE_DAY = "DAY";
	private final String STATE_NIGHT = "NIGHT";

	private String step;
	private String nextStep;
	private boolean previousVictims;
	private int cptCitizens;

	private CitizenControllerAgent ctrlAgent;
	private int nbPlayers;

	public TurnBehaviour(CitizenControllerAgent citizenControllerAgent) {
		super(citizenControllerAgent);

		this.ctrlAgent = citizenControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		//System.out.println("STATE = "+this.step);

		/** etat initial **/
		if(this.step.equals(STATE_INIT))
		{
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			this.nbPlayers = 0;
			previousVictims = false;
			cptCitizens = 0;

			this.nextStep = STATE_WAITING_START;
		}
		/** etat d'attente de d�but de tour **/
		else if(this.step.equals(STATE_WAITING_START))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("START_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_DAY;
			}
			else
			{
				block();
			}

		}else if(this.step.equals(STATE_DAY))
		{
			Functions.updateDayState("DAY", ctrlAgent, ctrlAgent.getGameid());
			this.nextStep = STATE_SEND_WAKE_ALL;
		}
		/** etat envoi des requ�tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ALL))
		{
			String[] args ={Status.SLEEP, Roles.CITIZEN};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = agents.size();
			System.out.println("nombre de joueurs to wake : "+this.nbPlayers);
			for(AID aid : agents)
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
				if(this.cptCitizens == this.nbPlayers)
				{
					this.cptCitizens = 0;
					this.ctrlAgent.doWait(500);
					this.nextStep = STATE_SEND_KILL_VICTIMS_REQUEST;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_WAKE_ALL;
					block();
				}
			}
			else
			{
				block();
			}
		}
		/** etat envoi requete pour tuer les  victimes d�sign�es durant la nuit **/
		else if(this.step.equals(STATE_SEND_KILL_VICTIMS_REQUEST))
		{
			this.ctrlAgent.addBehaviour(new KillVictimsBehaviour(this.ctrlAgent));
			this.nextStep = STATE_RECEIVE_KILL_VICTIMS_REQUEST;
		}
		/** etat pour recevoir la confirmation de la mort des victimes **/
		else if(this.step.equals(STATE_RECEIVE_KILL_VICTIMS_REQUEST))
		{
			/** le behaviour kill victims a termin� son travail **/
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
		/** etat envoi requete pour conna�tre l'etat de jeu **/
		else if(this.step.equals(STATE_SEND_CHECK_ENDGAME))
		{
			List<AID> agents = DFServices.findGameControllerAgent("GAME", this.ctrlAgent, this.ctrlAgent.getGameid());
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
		/** etat reception de l'�tat de jeu **/
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
			String [] args = {Roles.CITIZEN, Roles.MAYOR, Status.WAKE};
			List<AID> agents = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			//mayor ?
			if(agents.isEmpty())
			{
				List<String> choices = new ArrayList<String>();
				String [] argsDF = {Roles.CITIZEN, Status.WAKE};
				agents = DFServices.findGamePlayerAgent(argsDF, this.ctrlAgent, this.ctrlAgent.getGameid());
				this.nbPlayers = agents.size();

				for(AID aid : agents)
				{
					choices.add(aid.getName());
				}

				VoteRequest request = new VoteRequest();
				request.setRequest("ELECTION");
				request.setVoteAgainst(false);
				request.setChoices(choices);
				request.setVoters(choices);
				request.setCanBeFake(false);

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
			else
			{
				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
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
				/** r�cup�ration vote mayor **/		
				AID aid = new AID(message.getContent());

				/** msg attribution role **/
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(aid);

				messageRequest.setConversationId("ATTRIBUTION_ROLE");
				messageRequest.setContent("MAYOR");
				this.ctrlAgent.send(messageRequest);

				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
			else
			{
				block();
			}
		}
		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();

			String [] args = {Roles.CITIZEN, Status.WAKE};
			List<AID> agents = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = agents.size();

			for(AID aid : agents)
			{
				choices.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("CITIZEN_VOTE");
			request.setChoices(choices);
			request.setVoters(choices);
			request.setCanBeFake(true);

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
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setSender(this.getAgent().getAID());
				msg.addReceiver(this.getAgent().getAID());
				msg.setContent(victim);
				msg.setConversationId("ADD_VICTIM");
				this.ctrlAgent.send(msg);

				this.nextStep = STATE_SEND_KILL_VICTIMS_REQUEST;
			}
			else
			{
				block();
			}
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			String [] args = {Roles.CITIZEN, Status.WAKE};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());
			this.nbPlayers = agents.size();

			for(AID aid : agents)
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
				if(this.cptCitizens == this.nbPlayers)
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
			List<AID> agents = DFServices.findGameControllerAgent("GAME", this.ctrlAgent, this.ctrlAgent.getGameid());
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
