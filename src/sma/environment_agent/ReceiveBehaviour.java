package sma.environment_agent;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import sma.model.VoteResults;

public class ReceiveBehaviour extends OneShotBehaviour {
	
	private EnvironmentAgent envAgent;
	private ACLMessage message;
	

	public ReceiveBehaviour(EnvironmentAgent envAgent, ACLMessage message) {
		super();
		this.envAgent = envAgent;
		this.message = message;
	}


	@Override
	public void action() {
		
		System.out.println("RECEIVE DATA");
		
		ObjectMapper mapper = new ObjectMapper();
		
		if(message.getConversationId().equals("NEW_VOTE_RESULTS"))
		{
			try {
				VoteResults newVoteResults = mapper.readValue(message.getContent(), VoteResults.class);
				
				this.envAgent.setCurrentResults(newVoteResults);
				this.envAgent.getGlobalResults().add(newVoteResults);
				
				System.out.println("ENV AGENT | MAJ RESULTS ");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	

}
