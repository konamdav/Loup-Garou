package sma.environment_agent;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendBehaviour extends OneShotBehaviour {
	
	private EnvironmentAgent envAgent;
	private ACLMessage message;
	

	public SendBehaviour(EnvironmentAgent envAgent, ACLMessage message) {
		super();
		this.envAgent = envAgent;
		this.message = message;
	}


	@Override
	public void action() {
		System.out.println("SEND DATA");
		
		ACLMessage reply = message.createReply();
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		if(message.getConversationId().equals("GLOBAL_VOTE_RESULTS"))
		{
			try {
				json = mapper.writeValueAsString(this.envAgent.getGlobalResults());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		reply.setContent(json);
		this.envAgent.send(reply);
	}

	

}
