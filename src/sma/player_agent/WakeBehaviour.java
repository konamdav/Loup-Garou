package sma.player_agent;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.VoteRequest;

public class WakeBehaviour extends SimpleBehaviour{
	private PlayerAgent playerAgent ;

	public WakeBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
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
			this.playerAgent.setStatutandRegister("WAKE");

			//DFServices.setStatusPlayerAgent("WAKE", this.playerAgent, this.playerAgent.getGameid());
			
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("WAKE_PLAYER");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());

			this.myAgent.send(reply);
		}
		else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}
