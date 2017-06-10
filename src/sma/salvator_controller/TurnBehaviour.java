package sma.salvator_controller;

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
 * Behaviour de gestion du tour des Salvateurs
 * @author Kyrion
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
	private static final String STATE_SEND_SAVE_SOMEONE = "STATE_SEND_SAVE_SOMEONE";

	private String step;
	private String nextStep;

	private int cptSalvator;

	private SalvatorControllerAgent ctrlAgent;
	private int nbPlayers;
	private AID aidSavedOne;

	public TurnBehaviour(SalvatorControllerAgent salvatorControllerAgent) {
		super(salvatorControllerAgent);

		this.ctrlAgent = salvatorControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
		this.aidSavedOne = null;
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
			//this.aidSavedOne = null;

			cptSalvator = 0;
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
				block(1000);
			}

		}
		/** etat envoi des requ�tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ALL))
		{
			String[] args ={Status.SLEEP, Roles.SALVATOR};
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
				++this.cptSalvator;
				if(this.cptSalvator == this.nbPlayers)
				{
					this.cptSalvator = 0;
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

		/** etat reception des confirmations de reveil **/

		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();

			String [] args = {Roles.SALVATOR, Status.WAKE};
			List<AID> salvators = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = salvators.size();

			String [] args3 = {Roles.CITIZEN, Status.SLEEP};
			List<AID> possibleSaved = DFServices.findGamePlayerAgent(args3, this.ctrlAgent, this.ctrlAgent.getGameid());
			possibleSaved.addAll(salvators);

			for(AID aid : possibleSaved)
			{
				choices.add(aid.getName());
			}
			
			if(this.aidSavedOne!=null){
				choices.remove(this.aidSavedOne.getName());
			}

			for(AID aid : salvators)
			{
				voters.add(aid.getName());
			}

			if(!choices.isEmpty()){
			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(false);
			request.setRequest("PROTECT_VOTE");
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
			else
			{
				Functions.newActionToLog("Pas de protection possible ", this.getAgent(), this.ctrlAgent.getGameid());
				this.aidSavedOne = null;
				this.nextStep = STATE_SEND_SLEEP_ALL;
			}
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
				String saved = message.getContent();
				aidSavedOne = new AID(saved);

				Functions.newActionToLog("Les salvateurs prot�gent "+this.aidSavedOne.getLocalName(), this.getAgent(), this.ctrlAgent.getGameid());
				this.nextStep = STATE_SEND_SAVE_SOMEONE;
			}
			else
			{
				block(1000);
			}
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SAVE_SOMEONE))
		{
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("REMOVE_VICTIM");
			message.setContent(this.aidSavedOne.getName());
			message.setSender(this.ctrlAgent.getAID());
			
			List<AID> victims = DFServices.findGamePlayerAgent(Status.VICTIM, this.ctrlAgent, this.ctrlAgent.getGameid());
			if(victims.contains(this.aidSavedOne))
			{
				List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid());
				if(!agents.isEmpty())
				{
					
					Functions.newActionToLog("Les salvateurs prot�gent "+this.aidSavedOne.getLocalName(), this.getAgent(), this.ctrlAgent.getGameid());
					message.addReceiver(agents.get(0));
					this.ctrlAgent.send(message);

				}
			}

			this.nextStep = STATE_SEND_SLEEP_ALL;
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			String [] args = {Roles.SALVATOR, Status.WAKE};
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
				++this.cptSalvator;
				if(this.cptSalvator == this.nbPlayers)
				{
					this.cptSalvator = 0;
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
