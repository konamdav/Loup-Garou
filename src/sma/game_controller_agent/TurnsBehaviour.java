package sma.game_controller_agent;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic.interfaces.IController;
import sma.model.DFServices;


/***
 * Coordonne les tours
 * @author Davy
 *
 */
public class TurnsBehaviour extends SimpleBehaviour {
	private IController controllerAgent;

	private final static String STATE_INIT = "INIT";
	private final static String STATE_WAITING = "WAITING";
	private final static String STATE_START_CITIZEN_TURN = "START_CITIZEN_TURN";
	private final static String STATE_STOP_CITIZEN_TURN = "STOP_CITIZEN_TURN";

	private final static String STATE_PREINIT = "STATE_PREINIT";
	private final static String STATE_CHECK_ENDGAME_REQUEST = "STATE_CHECK_ENDGAME_REQUEST";
	private final static String STATE_CHECK_ENDGAME_RECEIVE = "STATE_CHECK_ENDGAME_RECEIVE";

	private final static String STATE_START_WEREWOLF_TURN = "START_WEREWOLF_TURN";
	private final static String STATE_STOP_WEREWOLF_TURN = "STOP_WEREWOLF_TURN";
	private final static String STATE_END = "END";

	private static final String STATE_POSTEND = "POSTEND";
	private boolean flag_done;

	private String step;
	private String nextStep;

	public TurnsBehaviour(IController controllerAgent) {
		super();

		this.step = STATE_PREINIT;
		this.nextStep = "";
		this.flag_done = false;
		this.controllerAgent = controllerAgent;	
	}

	@Override
	public void action() {

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
			this.nextStep = STATE_START_WEREWOLF_TURN;
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
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

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
