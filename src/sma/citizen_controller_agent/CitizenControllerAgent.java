package sma.citizen_controller_agent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.model.DFServices;

public class CitizenControllerAgent extends Agent {
	
	public CitizenControllerAgent() {
		super();	
	}
	
	
	@Override
	protected void setup() {
		
		DFServices.registerGameAgent("CONTROLLER", "CITIZEN", this, 0);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		
		
		//test init
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.addReceiver(this.getAID());			
		message.setConversationId("VOTE_REQUEST");
		this.send(message);
	}
}
