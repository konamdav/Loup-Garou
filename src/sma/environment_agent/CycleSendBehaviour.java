package sma.environment_agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour de reception de msg
 * @author Davy
 *
 */
public class CycleSendBehaviour extends CyclicBehaviour{
	private EnvironmentAgent envAgent;
	
	public CycleSendBehaviour(EnvironmentAgent envAgent) {
		super();
		this.envAgent = envAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			this.myAgent.addBehaviour(new SendBehaviour(this.envAgent, message));
		}
		else
		{
			block();
		}
				
	}

}
