package sma.werewolf_agent;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;
import sma.player_agent.PlayerAgent;

public class DeathTestBehaviour extends SimpleBehaviour{
	private PlayerAgent playerAgent ;

	public DeathTestBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("KILL_PLAYER"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			System.err.println("[ "+this.playerAgent.getName()+" ] DIE ");
			DFServices.setStatusPlayerAgent("DEAD", this.playerAgent, this.playerAgent.getGameid());

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
}
