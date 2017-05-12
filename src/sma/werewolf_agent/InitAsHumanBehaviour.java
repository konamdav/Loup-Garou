package sma.werewolf_agent;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_vote.HumanVoteBehaviour;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.PlayerAgent;

public class InitAsHumanBehaviour extends SimpleBehaviour{
	private PlayerAgent playerAgent ;

	public InitAsHumanBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("INIT_AS_HUMAN"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			
			System.err.println("AGENT BECAME HUMAN");
			HumanVoteBehaviour humanVoteBehaviour = new HumanVoteBehaviour(this.playerAgent);
			this.playerAgent.addBehaviour(humanVoteBehaviour);
			
			this.playerAgent.setHuman(true);
			
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("INIT_AS_HUMAN");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());
			this.playerAgent.send(reply);
		}
		else
		{
			block();
		}
	}

	@Override
	public boolean done() {
		return this.playerAgent.isHuman(); //TODO rajouter condition 
	}
}
