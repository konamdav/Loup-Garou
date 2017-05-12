package sma.lover_behaviour;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_vote.IVotingAgent;
import sma.model.ScoreResults;
import sma.model.VoteRequest;

public class LoverDeathBehaviour extends SimpleBehaviour{
	
	private String name_behaviour;

	public String getName_behaviour() {
		return name_behaviour;
	}

	public void setName_behaviour(String name_behaviour) {
		this.name_behaviour = name_behaviour;
	}

	//TODO Add behaviour Death for Lover 
	public LoverDeathBehaviour(IVotingAgent agent) {
		super();
		this.name_behaviour = "LOVER_DEATH_BEHAVIOUR";
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("DEATH_"+this.getName_behaviour()+"_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			System.out.print("Message received " + this.getBehaviourName() );
			
		}
		else {
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

}
