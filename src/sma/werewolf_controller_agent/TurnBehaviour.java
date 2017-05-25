package sma.werewolf_controller_agent;

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

	private final String STATE_SEND_WAKE_LITTLE_GIRL = "SEND_WAKE_LITTLE_GIRL";
	private final String STATE_RECEIVE_WAKE_LITTLE_GIRL= "RECEIVE_WAKE_LITTLE_GIRL";

	private final String STATE_SEND_SLEEP_LITTLE_GIRL = "SEND_SLEEP_LITTLE_GIRL";
	private final String STATE_RECEIVE_SLEEP_LITTLE_GIRL= "RECEIVE_SLEEP_LITTLE_GIRL";

	private final String STATE_MOVE_WEREWOLF = "MOVE_WEREWOLF";
	private final String STATE_MOVE_LITTLE_GIRL = "MOVE_LITTLE_GIRL";
	private final String STATE_INFORM_LITTLE_GIRL = "INFORM_LITTLE_GIRL";

	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";
	private final String STATE_SEND_SLEEP_ALL = "SEND_SLEEP_ALL";
	private final String STATE_RECEIVE_SLEEP_ALL = "RECEIVE_SLEEP_ALL";
	private static final String STATE_SEND_ADD_VICTIM = "ADD_VICTIM";

	private String step;
	private String nextStep;

	private int cptWerewolves;
	private int cptLittleGirl;

	private WerewolfControllerAgent ctrlAgent;
	private int nbPlayers;
	private AID aidVictim;

	public TurnBehaviour(WerewolfControllerAgent WerewolfControllerAgent) {
		super(WerewolfControllerAgent);

		this.ctrlAgent = WerewolfControllerAgent;
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
			this.aidVictim = null;

			cptWerewolves = 0;
			cptLittleGirl = 0;
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
			String[] args ={Status.SLEEP, Roles.WEREWOLF};
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
				++this.cptWerewolves;
				if(this.cptWerewolves == this.nbPlayers)
				{
					this.cptWerewolves = 0;
					this.nextStep = STATE_MOVE_WEREWOLF;
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

		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_MOVE_WEREWOLF))
		{
			String[] args = {Roles.WEREWOLF, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			for(AID werewolf : werewolves)
			{
				List<AID> neighbors = DFServices.findNeighbors(werewolf, this.ctrlAgent, this.ctrlAgent.getGameid());

				for(int i = 0; i<neighbors.size();++i)
				{
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setSender(this.ctrlAgent.getAID());
					message.addReceiver(neighbors.get(i));
					message.setConversationId("MOVE_WEREWOLF");

					if(i <= neighbors.size()/2)
					{
						message.setContent("LEFT");
					}
					else
					{
						message.setContent("RIGHT");
					}

					this.ctrlAgent.send(message);
				}
			}

			this.nextStep = STATE_SEND_WAKE_LITTLE_GIRL;

		}

		/** etat envoi des requ�tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_LITTLE_GIRL))
		{
			String[] args ={Status.SLEEP, Roles.LITTLE_GIRL};
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

			this.cptLittleGirl = 0;
			if(this.nbPlayers == 0)
			{
				System.err.println("!!!!!!!!!!!!!!!!");
				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_WAKE_LITTLE_GIRL;
			}
		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_RECEIVE_WAKE_LITTLE_GIRL))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptLittleGirl;
				if(this.cptLittleGirl == this.nbPlayers)
				{
					this.cptLittleGirl = 0;
					this.nextStep = STATE_MOVE_LITTLE_GIRL;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_WAKE_LITTLE_GIRL;
				}
			}
			else
			{
				block();
			}
		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_MOVE_LITTLE_GIRL))
		{
			String[] args = {Roles.LITTLE_GIRL, Status.WAKE};
			List<AID> littlegirls = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			System.err.println("---------> MOVE LITTLE");
			
			for(AID littlegirl : littlegirls)
			{
				List<AID> neighbors = DFServices.findNeighbors(littlegirl, this.ctrlAgent, this.ctrlAgent.getGameid());

				for(int i = 0; i<neighbors.size();++i)
				{
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setSender(this.ctrlAgent.getAID());
					message.addReceiver(neighbors.get(i));
					message.setConversationId("MOVE_LITTLE_GIRL");

					if(i <= neighbors.size()/2)
					{
						message.setContent("LEFT");
					}
					else
					{
						message.setContent("RIGHT");
					}

					this.ctrlAgent.send(message);
				}
			}

			this.nextStep = STATE_INFORM_LITTLE_GIRL;	
		}
		else if(this.step.equals(STATE_INFORM_LITTLE_GIRL))
		{

			System.err.println("---------> INFORM LITTLE GIRL");
			
			String [] args = {Roles.WEREWOLF, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = werewolves.size();
			AID werewolf = werewolves.get((int) (Math.random()*this.nbPlayers));

			String [] args2 = {Roles.LITTLE_GIRL, Status.WAKE};
			List<AID> littlegirls = DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid());


			ACLMessage messageRequest = new ACLMessage(ACLMessage.INFORM);
			messageRequest.setSender(this.ctrlAgent.getAID());
			for(AID aid : littlegirls)
			{
				messageRequest.addReceiver(aid);
			}
			messageRequest.setConversationId("IS_WEREWOLF");
			messageRequest.setContent(werewolf.getName());
			this.ctrlAgent.send(messageRequest);

			this.nextStep = STATE_SEND_SLEEP_LITTLE_GIRL;
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_LITTLE_GIRL))
		{
			String [] args = {Roles.LITTLE_GIRL, Status.WAKE};
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

			this.cptLittleGirl = 0;
			this.nextStep = STATE_RECEIVE_SLEEP_LITTLE_GIRL;
		}
		/** reception des confirmations de sommeil **/
		else if(this.step.equals(STATE_RECEIVE_SLEEP_LITTLE_GIRL))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptLittleGirl;
				if(this.cptLittleGirl == this.nbPlayers)
				{
					this.cptLittleGirl = 0;
					this.nextStep = STATE_SEND_VOTE_REQUEST;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_SLEEP_LITTLE_GIRL;
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

			String [] args = {Roles.WEREWOLF, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = werewolves.size();

			String [] args2 = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid());



			for(AID aid : citizens)
			{
				choices.add(aid.getName());
			}

			for(AID aid : werewolves)
			{
				voters.add(aid.getName());
			}

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("WEREWOLF_VOTE");
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
				String victim = message.getContent();
				aidVictim = new AID(victim);


				this.nextStep = STATE_SEND_ADD_VICTIM;
			}
			else
			{
				block();
			}
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_ADD_VICTIM))
		{
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("ADD_VICTIM");
			message.setContent(this.aidVictim.getName());
			message.setSender(this.ctrlAgent.getAID());

			List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				message.addReceiver(agents.get(0));
				this.ctrlAgent.send(message);
			}

			this.nextStep = STATE_SEND_SLEEP_ALL;
		}
		/** etat envoi des requ�tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ALL))
		{
			String [] args = {Roles.WEREWOLF, Status.WAKE};
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
				++this.cptWerewolves;
				if(this.cptWerewolves == this.nbPlayers)
				{
					this.cptWerewolves = 0;
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
