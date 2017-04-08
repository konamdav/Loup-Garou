package sma.environment_agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CycleReceiveBehaviour extends CyclicBehaviour{
	private EnvironmentAgent envAgent;
	
	public CycleReceiveBehaviour(EnvironmentAgent envAgent) {
		super();
		this.envAgent = envAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.AGREE),
				MessageTemplate.MatchConversationId("CURRENT_VOTE_RESULTS"));
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			this.myAgent.addBehaviour(new ReceiveBehaviour(this.envAgent, message));
		}
		else
		{
			block();
		}
				
	}

}
