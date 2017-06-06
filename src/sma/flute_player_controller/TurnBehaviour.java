package sma.flute_player_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
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

	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";
	private final String STATE_SEND_SLEEP_ALL = "SEND_SLEEP_ALL";
	private final String STATE_RECEIVE_SLEEP_ALL = "RECEIVE_SLEEP_ALL";
	private  final String STATE_SEND_ADD_CHARMED = "ADD_CHARMED";
	private  final String STATE_SEND_WAKE_CHARMED = "SEND_WAKE_CHARMED";
	private  final  String STATE_RECEIVE_WAKE_CHARMED = "RECEIVE_WAKE_CHARMED";
	private  final String STATE_SEND_SLEEP_CHARMED = "SEND_SLEEP_CHARMED";
	private  final String STATE_RECEIVE_WAIT_CHARMED = "RECEIVE_WAIT_CHARMED";
	private  final String STATE_RECEIVE_SLEEP_CHARMED = "RECEIVE_SLEEP_CHARMED";

	private String step;
	private String nextStep;

	private int cptFlutePlayers;

	private FlutePlayerControllerAgent ctrlAgent;
	private int nbPlayers;
	private AID aidChosen;

	public TurnBehaviour(FlutePlayerControllerAgent FlutePlayerControllerAgent) {
		super(FlutePlayerControllerAgent);

		this.ctrlAgent = FlutePlayerControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		/** etat initial **/
		if(this.step.equals(STATE_INIT))
		{
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			this.nbPlayers = 0;


			cptFlutePlayers = 0;

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
				this.nextStep = STATE_SEND_WAKE_ALL;
			}
			else
			{
				block();
			}

		}
		/** etat envoi des requ�tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ALL))
		{
			String[] args ={Status.SLEEP, Roles.FLUTE_PLAYER};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = agents.size();
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
				++this.cptFlutePlayers;
				if(this.cptFlutePlayers == this.nbPlayers)
				{
					this.cptFlutePlayers = 0;
					this.nextStep = STATE_SEND_VOTE_REQUEST;
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

		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();

			String [] args = {Roles.FLUTE_PLAYER, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = werewolves.size();

			String [] args2 = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid());

			
			String [] args3 = {Roles.CHARMED, Status.SLEEP};
			List<AID> charmed = DFServices.findGamePlayerAgent(args3, this.ctrlAgent, this.ctrlAgent.getGameid());
			citizens.removeAll(charmed); //David be carefull --'
			
			for(AID aid : citizens)
			{
				choices.add(aid.getName());
			}

			for(AID aid : werewolves)
			{
				voters.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(false);
			request.setRequest("FLUTE_PLAYER_VOTE");
			request.setChoices(choices);
			request.setVoters(voters);
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
				String chosen = message.getContent();
				this.aidChosen = new AID(chosen);

				this.nextStep = STATE_SEND_WAKE_CHARMED;
			}
			else
			{
				block();
			}
		}

		else if(this.step.equals(STATE_SEND_WAKE_CHARMED))
		{
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("WAKE_PLAYER");
			message.setSender(this.ctrlAgent.getAID());
			message.addReceiver(aidChosen);
			//message.setContent(Roles.CHARMED);
			this.ctrlAgent.send(message);

			this.nextStep = STATE_RECEIVE_WAKE_CHARMED;
		}
		else if(this.step.equals(STATE_RECEIVE_WAKE_CHARMED))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_ADD_CHARMED;
			}
			else
			{
				block();
			}
		}

		else if(this.step.equals(STATE_SEND_SLEEP_CHARMED))
		{
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("SLEEP_PLAYER");
			message.setSender(this.ctrlAgent.getAID());
			message.addReceiver(aidChosen);
			//message.setContent(Roles.CHARMED);
			this.ctrlAgent.send(message);

			this.nextStep = STATE_RECEIVE_SLEEP_CHARMED;
		}
		else if(this.step.equals(STATE_RECEIVE_SLEEP_CHARMED))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_SLEEP_ALL;
			}
			else
			{
				block();
			}
		}

		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_ADD_CHARMED))
		{
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);

			message.setConversationId("ATTRIBUTION_ROLE");
			message.setSender(this.ctrlAgent.getAID());
			message.addReceiver(aidChosen);
			message.setContent(Roles.CHARMED);
			this.ctrlAgent.send(message);

			this.nextStep = STATE_RECEIVE_WAIT_CHARMED;
		}
		else if(this.step.equals(STATE_RECEIVE_WAIT_CHARMED))
		{
			System.out.println("WAIT CHARMED");
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("ATTRIBUTION_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_SLEEP_CHARMED;
			}
			else
			{
				block();
			}
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			String [] args = {Roles.FLUTE_PLAYER, Status.WAKE};
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
				++this.cptFlutePlayers;
				if(this.cptFlutePlayers == this.nbPlayers)
				{
					this.cptFlutePlayers = 0;
					this.nextStep = STATE_END_TURN;
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
