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

public class LoverVoteBehaviour extends SimpleBehaviour{
	public LoverVoteBehaviour(IVotingAgent agent) {
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
			System.out.println("LOVER BEHAVIOUR : "+message.getContent());

			ObjectMapper mapper = new ObjectMapper();
			VoteRequest req = new VoteRequest();
			
			try {
				req = mapper.readValue(message.getContent(), VoteRequest.class);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			HashMap<String, Integer> results = new HashMap<String, Integer>();
			ScoreResults answer = new ScoreResults(results);
			
			for(String choice : req.getChoices())
			{
				results.put(choice, (int) (Math.random()*5));
			}
			
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("VOTE_INFORM");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(this.myAgent.getAID());
			
			String json = "";
			
			try {
				json = mapper.writeValueAsString(answer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			reply.setContent(json);
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
