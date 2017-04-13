package sma.citizen_controller_agent;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.generic.behaviour.SynchronousVoteBehaviour;
import sma.model.DFServices;

public class CitizenControllerAgent extends Agent {
	private int gameid;
	private Stack<AID> victims;
	
	
	public CitizenControllerAgent() {
		super();	
		
		victims = new Stack<AID>();
		
	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (int) args[0];
		
		DFServices.registerGameAgent("CONTROLLER", "CITIZEN", this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		this.addBehaviour(new AddVictimBehaviour(this));
		
		//test init
		/*ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.addReceiver(this.getAID());			
		message.setConversationId("VOTE_REQUEST");
		this.send(message);*/
		
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.addReceiver(this.getAID());			
		message.setConversationId("START_TURN");
		this.send(message);
	}


	public int getGameid() {
		return gameid;
	}
	
	
	public Stack<AID> getVictims() {
		return victims;
	}


}
