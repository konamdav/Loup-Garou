package sma.family_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ForceVoteRequest;
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

	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST= "RECEIVE_VOTE_REQUEST";

	private final String STATE_SEND_SLEEP_ALL = "SEND_SLEEP_ALL";
	private final String STATE_RECEIVE_SLEEP_ALL = "RECEIVE_SLEEP_ALL";

	private final String STATE_FIX_RESULT = "FIX_RESULT";


	private String step;
	private String nextStep;

	private int cptFamily;

	private FamilyControllerAgent ctrlAgent;
	private int nbPlayers;

	private AID aidChosen;

	public TurnBehaviour(FamilyControllerAgent FamilyControllerAgent) {
		super(FamilyControllerAgent);

		this.ctrlAgent = FamilyControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		//System.out.println("CC "+this.step);

		/** etat initial **/
		if(this.step.equals(STATE_INIT))
		{
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			this.nbPlayers = 0;
			this.cptFamily = 0;

			this.nextStep = STATE_WAITING_START;
		}
		/** etat d'attente de début de tour **/
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
				block(1000);
			}

		}
		/** etat envoi des requétes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ALL))
		{
			String[] args ={Status.SLEEP, Roles.FAMILY};
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
				++this.cptFamily;
				if(this.cptFamily == this.nbPlayers)
				{
					this.cptFamily = 0;
					this.nextStep = STATE_SEND_VOTE_REQUEST;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_WAKE_ALL;
				}
			}
			else
			{
				block(1000);
			}
		}

		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();

			String [] args = {Roles.FAMILY, Status.WAKE};
			List<AID> cupids = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = cupids.size();

			String [] args2 = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid());
			citizens.addAll(cupids);


			for(AID aid : citizens)
			{
				choices.add(aid.getName());
			}

			for(AID aid : cupids)
			{
				voters.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("CUPID_VOTE");
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

				this.nextStep = STATE_FIX_RESULT;
			}
			else
			{
				block(1000);
			}
		}
		/** etat envoi des requétes de sommeil **/
		else if(this.step.equals(STATE_FIX_RESULT))
		{
			String [] args = {Roles.FAMILY, Status.WAKE};
			//String [] args2 = {Roles.CUPID, Status.SLEEP};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());
			//agents.addAll(DFServices.findGamePlayerAgent( args2 , this.ctrlAgent, this.ctrlAgent.getGameid()));
			this.nbPlayers = agents.size();

			ForceVoteRequest rqst = new ForceVoteRequest();
			rqst.setVoteRequest("CITIZEN_VOTE");
			rqst.setVoteResult(this.aidChosen.getLocalName());

			Functions.newActionToLog("La famille désigne "+this.aidChosen.getLocalName(), this.ctrlAgent, this.ctrlAgent.getGameid());
			
			
			ObjectMapper mapper  = new ObjectMapper();
			for(AID aid : agents)
			{
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(aid);
				messageRequest.setConversationId("FORCE_VOTE");
				try {
					messageRequest.setContent(mapper.writeValueAsString(rqst));
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.myAgent.send(messageRequest);
			}

			this.nextStep = STATE_SEND_SLEEP_ALL;
		}
		/** etat envoi des requétes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			String [] args = {Roles.FAMILY, Status.WAKE};
			//String [] args2 = {Roles.CUPID, Status.SLEEP};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());
			//agents.addAll(DFServices.findGamePlayerAgent( args2 , this.ctrlAgent, this.ctrlAgent.getGameid()));
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
				++this.cptFamily;
				if(this.cptFamily == this.nbPlayers)
				{
					this.cptFamily = 0;
					this.nextStep = STATE_END_TURN;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_SLEEP_ALL;
				}
			}
			else
			{
				block(1000);
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
