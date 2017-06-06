package sma.witch_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

	private final String STATE_SEND_WAKE_ONE_WITCH = "SEND_WAKE_ONE_WITCH";
	private final String STATE_RECEIVE_WAKE_ONE_WITCH = "RECEIVE_WAKE_ONE_WITCH";

	private final String STATE_SEND_VOTE_ASK_DEATH_REQUEST = "STATE_SEND_VOTE_ASK_DEATH_REQUEST";
	private final String STATE_RECEIVE_VOTE_ASK_DEATH_REQUEST = "STATE_RECEIVE_VOTE_ASK_DEATH_REQUEST";

	private final String STATE_SEND_VOTE_ASK_LIFE_REQUEST = "STATE_SEND_VOTE_ASK_LIFE_REQUEST";
	private final String STATE_RECEIVE_VOTE_ASK_LIFE_REQUEST = "STATE_RECEIVE_VOTE_ASK_LIFE_REQUEST";

	private final String STATE_SEND_VOTE_DEATH_REQUEST = "SEND_VOTE_DEATH_REQUEST";
	private final String STATE_RECEIVE_VOTE_DEATH_REQUEST = "RECEIVE_VOTE_DEATH_REQUEST";

	private final String STATE_SEND_VOTE_LIFE_REQUEST = "STATE_SEND_VOTE_LIFE_REQUEST";
	private final String STATE_RECEIVE_VOTE_LIFE_REQUEST = "STATE_RECEIVE_VOTE_LIFE_REQUEST";

	private final String STATE_SEND_SLEEP_ONE_WITCH = "SEND_SLEEP_ONE_WITCH";
	private final String STATE_RECEIVE_SLEEP_ONE_WITCH = "RECEIVE_SLEEP_ONE_WITCH";



	private AID currentWitch;
	private AID playerChosen;
	private List<AID> witches;

	private String step;
	private String nextStep;

	private List<String> listUsedLife;
	private List<String> listUsedDeath;

	private int cptWitches;

	private WitchControllerAgent ctrlAgent;
	private int nbPlayers;


	public TurnBehaviour(WitchControllerAgent WitchControllerAgent) {
		super(WitchControllerAgent);

		this.ctrlAgent = WitchControllerAgent;

		this.listUsedLife = new ArrayList<String>();
		this.listUsedDeath = new ArrayList<String>();

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
			this.cptWitches = 0;
			this.currentWitch = null;
			this.playerChosen = null;
			this.witches = new ArrayList<AID>();

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
				String [] args = {Roles.WITCH, Status.SLEEP};
				this.witches = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());				
				this.nbPlayers = this.witches.size();

				String [] args2 = {Roles.WITCH};
				int nb =  DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid()).size();

				if(this.nbPlayers == 0 || (nb <= this.listUsedDeath.size() && nb <= this.listUsedLife.size()) )
				{
					//no more WITCH or all powers are used
					this.nextStep = STATE_END_TURN;
				}
				else
				{	
					this.nextStep = STATE_SEND_WAKE_ONE_WITCH;
				}
			}
			else
			{
				this.nextStep = STATE_WAITING_START;
				block(1000);
			}

		}
		/** etat envoi des requï¿½tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ONE_WITCH))
		{
			this.currentWitch = this.witches.get(this.cptWitches);		
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.currentWitch);
			messageRequest.setConversationId("WAKE_PLAYER");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_WAKE_ONE_WITCH;
		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_RECEIVE_WAKE_ONE_WITCH))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_VOTE_ASK_LIFE_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_WAKE_ONE_WITCH;
				block(1000);
			}
		}
		else if(this.step.equals(STATE_SEND_VOTE_ASK_LIFE_REQUEST))
		{	
			if(!this.listUsedLife.contains(this.currentWitch.getName()))
			{

				List<String> choices = new ArrayList<String>();
				List<String> voters = new ArrayList<String>();

				String [] args = {Status.VICTIM};
				List<AID> victims = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());
				List<AID> selections = new ArrayList<AID>();

				for(AID aid : victims)
				{
					choices.add(aid.getName());
				}

				if(!choices.isEmpty()){

					voters.add(this.currentWitch.getName());

					VoteRequest request = new VoteRequest();
					request.setVoteAgainst(false);
					request.setRequest("ASKLIFE_VOTE");
					request.setChoices(choices);
					request.setVoters(voters);
					request.setCanBeFake(false);
					request.setAskRequest(true);

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

					this.nextStep = STATE_RECEIVE_VOTE_ASK_LIFE_REQUEST;
				}
				else
				{
					this.nextStep = STATE_SEND_VOTE_ASK_DEATH_REQUEST;
				}
			}
			else
			{

				this.nextStep = STATE_SEND_VOTE_ASK_DEATH_REQUEST;//STATE_SEND_SLEEP_ONE_WITCH;

			}
		}
		else if(this.step.equals(STATE_SEND_VOTE_ASK_DEATH_REQUEST))
		{	
			if(!this.listUsedDeath.contains(this.currentWitch.getName()))
			{

				List<String> choices = new ArrayList<String>();
				List<String> voters = new ArrayList<String>();

				String [] args = {Roles.CITIZEN, Status.SLEEP};
				List<AID> citizens = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());
				List<AID> selections = new ArrayList<AID>();

				for(AID aid : citizens)
				{
					choices.add(aid.getName());
				}

				voters.add(this.currentWitch.getName());

				VoteRequest request = new VoteRequest();
				request.setVoteAgainst(true);
				request.setRequest("ASKDEATH_VOTE");
				request.setChoices(choices);
				request.setVoters(voters);
				request.setCanBeFake(false);
				request.setAskRequest(true);

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

				this.nextStep = STATE_RECEIVE_VOTE_ASK_DEATH_REQUEST;
			}
			else
			{

				this.nextStep = STATE_SEND_SLEEP_ONE_WITCH;

			}
		}
		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_ASK_LIFE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String answer = message.getContent();
				if(answer.equals("OK"))
				{
					this.listUsedLife.add(this.currentWitch.getName());
					this.nextStep = STATE_SEND_VOTE_LIFE_REQUEST;
				}
				else
				{
					this.nextStep = STATE_SEND_VOTE_ASK_DEATH_REQUEST;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_VOTE_ASK_LIFE_REQUEST;
				block(1000);
			}
		}

		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_ASK_DEATH_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String answer = message.getContent();
				if(answer.equals("OK"))
				{
					this.listUsedDeath.add(this.currentWitch.getName());
					this.nextStep = STATE_SEND_VOTE_DEATH_REQUEST;
				}
				else
				{
					this.nextStep = STATE_SEND_SLEEP_ONE_WITCH;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_VOTE_ASK_DEATH_REQUEST;
				block(1000);
			}
		}
		else if(this.step.equals(STATE_SEND_VOTE_DEATH_REQUEST))
		{	


			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();

			String [] args = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());
			List<AID> selections = new ArrayList<AID>();

			for(AID aid : citizens)
			{
				choices.add(aid.getName());
			}

			voters.add(this.currentWitch.getName());

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("DEATH_VOTE");
			request.setChoices(choices);
			request.setVoters(voters);
			request.setCanBeFake(false);
			request.setAskRequest(false);

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
			this.nextStep = STATE_RECEIVE_VOTE_DEATH_REQUEST;

		}
		else if(this.step.equals(STATE_SEND_VOTE_LIFE_REQUEST))
		{	

			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();
			String [] args = {Status.VICTIM};
			List<AID> victims = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());
			List<AID> selections = new ArrayList<AID>();

			for(AID aid : victims)
			{
				choices.add(aid.getName());
			}
			voters.add(this.currentWitch.getName());

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("LIFE_VOTE");
			request.setChoices(choices);
			request.setVoters(voters);
			request.setCanBeFake(false);
			request.setAskRequest(false);

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
			this.nextStep = STATE_RECEIVE_VOTE_LIFE_REQUEST;

		}


		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_LIFE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String player = message.getContent();
				if(!player.isEmpty())
				{
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("REMOVE_VICTIM");
					message.setContent(player);
					message.setSender(this.ctrlAgent.getAID());

					List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid());
					if(!agents.isEmpty())
					{
						message.addReceiver(agents.get(0));
						this.ctrlAgent.send(message);
					}
				}

				this.nextStep = STATE_SEND_VOTE_ASK_DEATH_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_VOTE_LIFE_REQUEST;
				block(1000);
			}
		}

		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_DEATH_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String player = message.getContent();
				if(!player.isEmpty())
				{
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("ADD_VICTIM");
					message.setContent(player);
					message.setSender(this.ctrlAgent.getAID());

					List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid());
					if(!agents.isEmpty())
					{
						message.addReceiver(agents.get(0));
						this.ctrlAgent.send(message);
					}
				}

				this.nextStep = STATE_SEND_SLEEP_ONE_WITCH;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_VOTE_DEATH_REQUEST;
				block(1000);
			}
		}


		/** etat envoi des requï¿½tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ONE_WITCH))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.currentWitch);
			messageRequest.setConversationId("SLEEP_PLAYER");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_SLEEP_ONE_WITCH;
		}
		/** reception des confirmations de sommeil **/
		else if(this.step.equals(STATE_RECEIVE_SLEEP_ONE_WITCH))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptWitches;
				if(this.cptWitches< this.nbPlayers)
				{
					this.nextStep = STATE_SEND_WAKE_ONE_WITCH;
				}
				else
				{
					this.nextStep = STATE_END_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_SLEEP_ONE_WITCH;
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
