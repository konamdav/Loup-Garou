package sma.environmenthuman_agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour de reception de msg
 * @author Davy
 *
 */
public class CycleSendBehaviour extends CyclicBehaviour{
	private EnvironmentHumanAgent envAgent;
	
	public CycleSendBehaviour(EnvironmentHumanAgent envAgent) {
		super();
		this.envAgent = envAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			//System.err.println("[ENV "+this.envAgent.getLocalName()+"] Message rqst "+message.getConversationId());
			this.myAgent.addBehaviour(new SendBehaviour(this.envAgent, message));
		}
		else
		{
			block(1000);
		}
				
	}

}
