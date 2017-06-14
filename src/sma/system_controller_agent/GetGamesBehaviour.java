package sma.system_controller_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;

/***
 * Transmission des jeux en cours
 * @author Davy
 *
 */
public class GetGamesBehaviour extends Behaviour {
	private SystemControllerAgent systemControllerAgent;

	public GetGamesBehaviour(SystemControllerAgent systemControllerAgent) {
		super();
		this.systemControllerAgent = systemControllerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("GET_GAMES"));

		
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("GET_GAMES");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());
			
			ObjectMapper mapper = new ObjectMapper();
			List<Integer> containers = new ArrayList<Integer>();
			
			for(GameContainer container : this.systemControllerAgent.getContainers())
			{
				containers.add(container.getGameid());
			}
			
			String json ="";
			try {
				json = mapper.writeValueAsString(containers);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reply.setContent(json);
			//System.err.println(json);
			this.systemControllerAgent.send(reply);
			
		}

	}

	@Override
	public boolean done() {
		return false;
	}

}
