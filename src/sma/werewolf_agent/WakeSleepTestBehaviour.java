package sma.werewolf_agent;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;

public class WakeSleepTestBehaviour extends SimpleBehaviour{
	public WakeSleepTestBehaviour(IVotingAgent agent) {
		super();
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("WAKE_PLAYER"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			System.out.println("I WAKE ");
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("WAKE_PLAYER");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());

			this.myAgent.send(reply);
		}
		else
		{
			mt = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			 message = this.myAgent.receive(mt);
			if (message != null) 
			{
				System.out.println("I SLEEP ");
				ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
				reply.setConversationId("SLEEP_PLAYER");
				reply.setSender(this.myAgent.getAID());
				reply.addReceiver(message.getSender());

				this.myAgent.send(reply);
			}
			else{
				block();
			}		
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}
