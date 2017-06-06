package sma.medium_controller;

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

	private final String STATE_SEND_WAKE_ONE_MEDIUM = "SEND_WAKE_ONE_MEDIUM";
	private final String STATE_RECEIVE_WAKE_ONE_MEDIUM = "RECEIVE_WAKE_ONE_MEDIUM";

	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";

	private final String STATE_SEND_SLEEP_ONE_MEDIUM = "SEND_SLEEP_ONE_MEDIUM";
	private final String STATE_RECEIVE_SLEEP_ONE_MEDIUM = "RECEIVE_SLEEP_ONE_MEDIUM";
	private static final String STATE_GET_ROLE_REQUEST = "GET_ROLE_REQUEST";
	private static final String STATE_GET_ROLE_RECEIVE= "GET_ROLE_RECEIVE";

	private AID currentMedium;
	private AID playerChosen;
	private List<AID> mediums;

	private String step;
	private String nextStep;

	private HashMap<String, List<String>> archiveMedium;
	private int cptMediums;

	private MediumControllerAgent ctrlAgent;
	private int nbPlayers;


	public TurnBehaviour(MediumControllerAgent MediumControllerAgent) {
		super(MediumControllerAgent);

		this.ctrlAgent = MediumControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";

		this.archiveMedium = new HashMap<String, List<String>>();
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
			this.cptMediums = 0;
			this.currentMedium = null;
			this.playerChosen = null;
			this.mediums = new ArrayList<AID>();

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
				String [] args = {Roles.MEDIUM, Status.SLEEP};
				this.mediums = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());				
				this.nbPlayers = this.mediums.size();

				if(this.nbPlayers == 0)
				{
					//no more medium
					this.nextStep = STATE_END_TURN;
				}
				else
				{	
					this.nextStep = STATE_SEND_WAKE_ONE_MEDIUM;
				}
			}
			else
			{
				this.nextStep = STATE_WAITING_START;
				block(1000);
			}

		}
		/** etat envoi des requï¿½tes de reveil pour tout les joueurs**/
		else if(this.step.equals(STATE_SEND_WAKE_ONE_MEDIUM))
		{
			this.currentMedium = this.mediums.get(this.cptMediums);		
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.currentMedium);
			messageRequest.setConversationId("WAKE_PLAYER");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_WAKE_ONE_MEDIUM;
		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_RECEIVE_WAKE_ONE_MEDIUM))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_WAKE_ONE_MEDIUM;
				block(1000);
			}
		}

		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();


			String [] args = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());
			List<AID> selections = new ArrayList<AID>();


			if(this.archiveMedium.containsKey(this.currentMedium.getName()))
			{
				List<String> list = this.archiveMedium.get(this.currentMedium.getName());
				for(AID aid : citizens)
				{
					if(!list.contains(aid.getName()))
					{
						selections.add(aid);
					}
				}
				citizens = selections;
			}

			if(!citizens.isEmpty()){
				for(AID aid : citizens)
				{
					choices.add(aid.getName());
				}


				voters.add(this.currentMedium.getName());

				VoteRequest request = new VoteRequest();
				request.setVoteAgainst(true);
				request.setRequest("MEDIUM_VOTE");
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
				//fin de tour car il n'a persone à choisir
				this.nextStep = STATE_SEND_SLEEP_ONE_MEDIUM;
				
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
				String chosen = message.getContent();
				this.playerChosen = new AID(chosen);

				//maj list chosen
				List<String> list = new ArrayList<String>();
				if(this.archiveMedium.containsKey(this.currentMedium.getName()))
				{
					list = this.archiveMedium.get(this.currentMedium.getName());
				}

				list.add(this.playerChosen.getName());
				this.archiveMedium.put(this.currentMedium.getName(), list);

				this.nextStep = STATE_GET_ROLE_REQUEST;

			}
			else
			{
				this.nextStep = STATE_RECEIVE_VOTE_REQUEST;
				block(1000);
			}
		}
		/** etat reception du vote **/
		else if(this.step.equals(STATE_GET_ROLE_REQUEST))
		{
			System.err.println("REQUEST GET ROLE OF "+this.playerChosen.getName()+" BY "+this.currentMedium.getName());

			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("GET_ROLE");
			message.setSender(this.ctrlAgent.getAID());
			message.addReceiver(playerChosen);

			this.ctrlAgent.send(message);

			this.nextStep = STATE_GET_ROLE_RECEIVE;
		}	
		/** etat reception du vote **/
		else if(this.step.equals(STATE_GET_ROLE_RECEIVE))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("GET_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String role = message.getContent();
				if(role.equals(Roles.WEREWOLF))
				{
					System.err.println(this.playerChosen.getName()+" is a werewolf");
					message = new ACLMessage(ACLMessage.INFORM);
					message.setConversationId("IS_WEREWOLF");
					message.setContent(this.playerChosen.getName());

					message.setSender(this.ctrlAgent.getAID());
					message.addReceiver(this.currentMedium);

					this.ctrlAgent.send(message);					
				}
				else
				{
					System.err.println(this.playerChosen.getName()+" is a citizen");
					message = new ACLMessage(ACLMessage.INFORM);
					message.setConversationId("IS_CITIZEN");
					message.setContent(this.playerChosen.getName());

					message.setSender(this.ctrlAgent.getAID());
					message.addReceiver(this.currentMedium);

					this.ctrlAgent.send(message);	
				}

				this.nextStep = STATE_SEND_SLEEP_ONE_MEDIUM;

			}
			else
			{
				this.nextStep = STATE_GET_ROLE_RECEIVE;
				block(1000);
			}
		}	

		/** etat envoi des requï¿½tes de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ONE_MEDIUM))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.currentMedium);
			messageRequest.setConversationId("SLEEP_PLAYER");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_SLEEP_ONE_MEDIUM;
		}
		/** reception des confirmations de sommeil **/
		else if(this.step.equals(STATE_RECEIVE_SLEEP_ONE_MEDIUM))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				++this.cptMediums;
				if(this.cptMediums< this.nbPlayers)
				{
					this.nextStep = STATE_SEND_WAKE_ONE_MEDIUM;
				}
				else
				{
					this.nextStep = STATE_END_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_SLEEP_ONE_MEDIUM;
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
