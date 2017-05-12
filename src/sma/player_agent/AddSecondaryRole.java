package sma.player_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.environment_agent.ReceiveBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;

public class AddSecondaryRole extends CyclicBehaviour{
	
	//TODO REGISTER TO DFSERVICES 
	//TODO Later
	
	
	private PlayerAgent agent;
	public AddSecondaryRole(PlayerAgent agent){
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("NEW_MAIN_ROLE"));
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			System.out.println("WAIT NEW ROLE BEHAVIOUR : "+message.getContent());

			ObjectMapper mapper = new ObjectMapper();
			Roles role = null;
			
			try {
				role = mapper.readValue(message.getContent(), Roles.class);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			ACLMessage reply = null;
			//Test if already got a role
			/*if (this.agent.getRoles() == {"Citizen"}){
				System.out.println("SET NEW ROLE : "+role+ "TO THIS PLAYER "+this.agent.getName());
				this.agent.setMain_role(role);
				
				reply = new ACLMessage(ACLMessage.CONFIRM);

			}
			else if (this.agent.getMain_role() == role){
				System.out.println("Already got this role "+role+ "TO THIS PLAYER "+this.agent.getName());
				
				reply = new ACLMessage(ACLMessage.FAILURE);

			}
			else if (this.agent.getMain_role() != role){
				System.out.println("Got another role "+this.agent.getMain_role()+ "TO THIS PLAYER "+this.agent.getName());
				
				reply = new ACLMessage(ACLMessage.FAILURE);
			}*/
			
			/*reply.setConversationId("NEW_ROLE");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());*/
		}
		
		
	}

}
