package sma.lover_behaviour;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;

public class LoverDeathBehaviour extends SimpleBehaviour{
	
	//TODO TODO Add behaviour Death for Lover 
	public LoverDeathBehaviour(IVotingAgent agent) {
		super();
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("VOTE_LOVER_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
		
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
