package sma.game_controller_agent;

import java.util.List;

import generic.agent.IController;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;

public class TurnsBehaviour extends SimpleBehaviour {
	private IController controllerAgent;
	private final static String STATE_INIT = "INIT";
	private final static String STATE_WAITING = "WAITING";
	private final static String STATE_START_CITIZEN_TURN = "START_CITIZEN_TURN";
	private final static String STATE_STOP_CITIZEN_TURN = "STOP_CITIZEN_TURN";
	private final static String STATE_START_WEREWOLF_TURN = "START_WEREWOLF_TURN";
	private final static String STATE_STOP_WEREWOLF_TURN = "STOP_WEREWOLF_TURN";
	private final static String STATE_END = "END";
	
	
	private String step;
	private String nextStep;
	
	public TurnsBehaviour(IController controllerAgent) {
		super();
		
		this.step = STATE_INIT;
		this.nextStep = "";
		
		this.controllerAgent = controllerAgent;	
	}

	@Override
	public void action() {
		
		if(this.step.equals(STATE_INIT))
		{
			
			this.nextStep = STATE_START_CITIZEN_TURN;
		}
		else if (this.step.equals(STATE_START_CITIZEN_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.myAgent, this.controllerAgent.getGameid());
			if(agents.isEmpty())
			{
				this.nextStep = STATE_START_CITIZEN_TURN;
			}
			else {
				
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
				this.nextStep = STATE_START_WEREWOLF_TURN;
			}
			else
			{
				block();
			}

		}
		else if (this.step.equals(STATE_START_WEREWOLF_TURN))
		{
			
			this.nextStep = STATE_STOP_WEREWOLF_TURN;
		}
		else if (this.step.equals(STATE_STOP_WEREWOLF_TURN))
		{
			
			this.nextStep = STATE_END;
		}
		else if (this.step.equals(STATE_END))
		{
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			
			
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
