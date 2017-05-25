package sma.game_controller_agent;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;


/***
 * Coordonne les tours
 * @author Davy
 *
 */
public class TurnsBehaviour extends SimpleBehaviour {
	private GameControllerAgent controllerAgent;
	private final String STATE_INIT = "INIT";
	private final String STATE_WAITING = "WAITING";
	private final String STATE_START_CITIZEN_TURN = "START_CITIZEN_TURN";
	private final String STATE_STOP_CITIZEN_TURN = "STOP_CITIZEN_TURN";

	private final String STATE_PREINIT = "STATE_PREINIT";
	private final String STATE_CHECK_ENDGAME_REQUEST = "STATE_CHECK_ENDGAME_REQUEST";
	private final String STATE_CHECK_ENDGAME_RECEIVE = "STATE_CHECK_ENDGAME_RECEIVE";

	private final String STATE_START_WEREWOLF_TURN = "START_WEREWOLF_TURN";
	private final String STATE_STOP_WEREWOLF_TURN = "STOP_WEREWOLF_TURN";
	
	private final String STATE_START_FLUTE_PLAYER_TURN = "START_FLUTE_PLAYER_TURN";
	private final String STATE_STOP_FLUTE_PLAYER_TURN = "STOP_FLUTE_PLAYER_TURN";

	private final String STATE_START_CUPID_TURN = "START_CUPID_TURN";
	private final String STATE_STOP_CUPID_TURN = "STOP_CUPID_TURN";
	
	private final String STATE_START_MEDIUM_TURN = "START_MEDIUM_TURN";
	private final String STATE_STOP_MEDIUM_TURN = "STOP_MEDIUM_TURN";

	private final String STATE_END = "END";

	private static final String STATE_POSTEND = "POSTEND";
	private boolean flag_done;

	private boolean flag_cupid; 
	
	private String step;
	private String nextStep;

	public TurnsBehaviour(GameControllerAgent controllerAgent) {
		super();

		this.step = STATE_PREINIT;
		this.nextStep = "";
		this.flag_cupid = false;
		this.flag_done = false;
		this.controllerAgent = controllerAgent;	
	}

	@Override
	public void action() {

		//System.out.println("STATE GC = "+this.step);
		
		if(this.step.equals(STATE_PREINIT))
		{	
			this.nextStep = STATE_CHECK_ENDGAME_REQUEST;
		}
		else if(this.step.equals(STATE_CHECK_ENDGAME_REQUEST))
		{	
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setSender(this.myAgent.getAID());
			message.addReceiver(this.myAgent.getAID());
			message.setConversationId("CHECK_END_GAME");
			this.myAgent.send(message);

			this.nextStep = STATE_CHECK_ENDGAME_RECEIVE;
		}
		else if(this.step.equals(STATE_CHECK_ENDGAME_RECEIVE))
		{	
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("CONTINUE_GAME"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				System.err.println("RECEIVE CONTINUE GAME");
				this.nextStep = STATE_INIT;
			}
			else
			{
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("END_GAME"));

				message = this.myAgent.receive(mt);
				if(message!=null)
				{
					this.nextStep = STATE_POSTEND;
				}
				else
				{
					this.nextStep = STATE_CHECK_ENDGAME_RECEIVE;
					block();
				}
			}
		}
		else if(this.step.equals(STATE_INIT))
		{	
			
			System.gc();
			
			if(this.controllerAgent.isCheckEndGame())
			{
				this.nextStep = STATE_POSTEND;
			}
			else
			{
				this.controllerAgent.incTurn();
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.err.println("DAY "+this.controllerAgent.getNum_turn());
				this.nextStep = STATE_START_CUPID_TURN;
			}
		}
		else if (this.step.equals(STATE_START_CUPID_TURN))
		{
			
			List<AID> agents = DFServices.findGameControllerAgent(Roles.CUPID, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.CUPID, Status.SLEEP};
				List<AID> cupids = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = cupids.size();

				if(nbPlayers > 0 && !this.flag_cupid)
				{
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.flag_cupid = true;
					this.nextStep = STATE_STOP_CUPID_TURN;
				}
				else
				{
					this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
			}
			

		}
		else if (this.step.equals(STATE_STOP_CUPID_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_CUPID_TURN;
				block();
			}

		}
		
		else if (this.step.equals(STATE_START_FLUTE_PLAYER_TURN))
		{
			
			List<AID> agents = DFServices.findGameControllerAgent(Roles.FLUTE_PLAYER, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.FLUTE_PLAYER, Status.SLEEP};
				List<AID> mediums = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = mediums.size();

				if(nbPlayers > 0)
				{
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_FLUTE_PLAYER_TURN;
				}
				else
				{
					this.nextStep = STATE_START_MEDIUM_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_MEDIUM_TURN;
			}
			

		}
		else if (this.step.equals(STATE_STOP_FLUTE_PLAYER_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_START_MEDIUM_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_FLUTE_PLAYER_TURN;
				block();
			}

		}
		
		else if (this.step.equals(STATE_START_MEDIUM_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent(Roles.MEDIUM, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.MEDIUM, Status.SLEEP};
				List<AID> mediums = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = mediums.size();

				if(nbPlayers > 0)
				{
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_MEDIUM_TURN;
				}
				else
				{
					this.nextStep = STATE_START_WEREWOLF_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_WEREWOLF_TURN;
			}

		}
		else if (this.step.equals(STATE_STOP_MEDIUM_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_START_WEREWOLF_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_MEDIUM_TURN;
				block();
			}

		}
		else if (this.step.equals(STATE_START_CITIZEN_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{				
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("START_TURN");
				message.setSender(this.getAgent().getAID());
				message.addReceiver(agents.get(0));
				this.getAgent().send(message);

				this.nextStep = STATE_STOP_CITIZEN_TURN;
			}

		}
		else if (this.step.equals(STATE_STOP_CITIZEN_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_END;
			}
			else
			{
				this.nextStep = STATE_STOP_CITIZEN_TURN;
				block();
			}

		}
		else if (this.step.equals(STATE_START_WEREWOLF_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent("WEREWOLF", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{				
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("START_TURN");
				message.setSender(this.getAgent().getAID());
				message.addReceiver(agents.get(0));
				this.getAgent().send(message);

				this.nextStep = STATE_STOP_WEREWOLF_TURN;
			}
			this.nextStep = STATE_STOP_WEREWOLF_TURN;

		}
		else if (this.step.equals(STATE_STOP_WEREWOLF_TURN))
		{

			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_START_CITIZEN_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_WEREWOLF_TURN;
				block();
			}

		}
		else if (this.step.equals(STATE_END))
		{
			ObjectMapper mapper = new ObjectMapper();
			DFServices.getPlayerProfiles(this.myAgent, this.controllerAgent.getGameid());


			this.nextStep = STATE_INIT;
		}
		else if (this.step.equals(STATE_POSTEND))
		{
			System.err.println("FINISH TURNS");
			this.flag_done = true;
		}

		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return this.flag_done;
	}
}
