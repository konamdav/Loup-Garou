package sma.system_controller_agent;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;
import sma.model.GameSettings;

public class ReturnContainersLenghtBehaviour extends CyclicBehaviour {
	private SystemControllerAgent systemControllerAgent;

	public ReturnContainersLenghtBehaviour(SystemControllerAgent systemControllerAgent) {
		super();
		this.systemControllerAgent = systemControllerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
				MessageTemplate.MatchConversationId("CONTAINERS_LENGHT"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			ACLMessage reply = message.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent(Integer.toString(this.systemControllerAgent.getContainers().size()));
			getAgent().send(reply);
		}
		else
		{
			block();
		}
	}
}