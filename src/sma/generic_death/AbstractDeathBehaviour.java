package sma.generic_death;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

public class AbstractDeathBehaviour extends SimpleBehaviour{
	private PlayerAgent agent;

	public AbstractDeathBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("KILL_PLAYER"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			System.err.println("[ "+this.agent.getName()+" ] DIE ");
			
			
			delete_behaviour();
			
			this.agent.setStatutandRegister("DEAD");
			DFServices.deregisterPlayerAgent("VICTIM", this.myAgent, this.agent.getGameid()); //retire son statut de victime (car il est mort)
			
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("DEAD_PLAYER");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());

			this.myAgent.send(reply);
		}
		else
		{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}
	
	public void delete_behaviour() {

		//Copy this technique from AbstractVoteBehvaiour

		//TODO Look if better to pass to oneshotBehaviour
		for(String s : this.agent.getDeathBehaviours())
		{
			System.out.println("ROLE VOTE : "+s);

			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.agent.getAID());
			messageRequest.addReceiver(this.agent.getAID());
			messageRequest.setConversationId("DEATH_"+s+"_REQUEST");

			this.myAgent.send(messageRequest);
		}
	}

}
