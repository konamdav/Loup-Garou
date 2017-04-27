package sma.generic_agent;

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
import sma.player_agent.PlayerAgent;

public class NewMainRoleBehaviour extends CyclicBehaviour{
	
	//TODO REGISTER TO DFSERVICES 
	
	private PlayerAgent agent;
	public NewMainRoleBehaviour(PlayerAgent agent){
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		//Message get from InitBehaviour game_control_Agent
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("ATTRIBUTION_ROLE"));
		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			System.out.println("WAIT NEW MAIN ROLE BEHAVIOUR : "+message.getContent());

			ObjectMapper mapper = new ObjectMapper();
			String new_role = message.getContent();
			
			ACLMessage reply = null;
			//Test if already got a role
			String main_role = this.agent.getMain_role();
			
			if (main_role == null){
				System.out.println("SET NEW ROLE : "+new_role+ " TO THIS PLAYER "+this.agent.getName());				
				
				this.agent.setMain_role(new_role);
				//TODO Check if in first position
				//DFServices.modifyPlayerAgent(old_role, new_role, this.agent, this.agent.getGameid());
				DFServices.registerPlayerAgent(this.agent.getMain_role(), this.agent, this.agent.getGameid());
				reply = new ACLMessage(ACLMessage.CONFIRM);
				
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.addReceiver(this.agent.getAID());
				messageRequest.setConversationId("INIT_ROLE");

				messageRequest.setContent(this.agent.getMain_role());
				this.myAgent.send(messageRequest);
				
				
			}
			else if (main_role == new_role){
				System.out.println("Already got this role "+new_role+ "TO THIS PLAYER "+this.agent.getName());
			
				reply = new ACLMessage(ACLMessage.FAILURE);
			}
			else if (main_role != new_role){
				//TODO If voleur can swap role SO DO this case 
				System.out.println("Got another role "+main_role+ "TO THIS PLAYER "+this.agent.getName());
				reply = new ACLMessage(ACLMessage.FAILURE);
			}

			reply.setConversationId("NEW_ROLE");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());
			
			//this.agent.send(reply); TODO reply not treated in gameControlleur
		}
		
		
	}

}
