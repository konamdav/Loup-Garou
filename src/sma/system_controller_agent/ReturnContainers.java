package sma.system_controller_agent;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;
import sma.model.GameSettings;

public class ReturnContainers extends CyclicBehaviour {
	private SystemControllerAgent systemControllerAgent;

	public ReturnContainers(SystemControllerAgent systemControllerAgent) {
		super();
		this.systemControllerAgent = systemControllerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
				MessageTemplate.MatchConversationId("CONTAINERS"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println("ok1");
				String jsonInString = mapper.writeValueAsString(this.systemControllerAgent.getIdContainers());
				System.out.println("ok2");
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(jsonInString);
				getAgent().send(reply);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			block(1000);
		}
	}
}