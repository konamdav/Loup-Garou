package sma.environmenthuman_agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/***
 * Behaviour d'envoi de données
 * @author Davy
 *
 */
public class CycleReceiveBehaviour extends CyclicBehaviour{
	private EnvironmentHumanAgent envAgent;
	
	public CycleReceiveBehaviour(EnvironmentHumanAgent envAgent) {
		super();
		this.envAgent = envAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			//System.err.println("[ENV] Message rcv "+message.getConversationId());
			this.myAgent.addBehaviour(new ReceiveBehaviour(this.envAgent, message));
		}
		else
		{
			block(1000);
		}
				
	}

}
