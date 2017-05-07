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
		
		if(message.getConversationId().equals("NEW_CITIZEN_VOTE_RESULTS"))
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
		else if(message.getConversationId().equals("NEW_VOTE_RESULTS"))
		{
			try {
				VoteResults newVoteResults = mapper.readValue(message.getContent(), VoteResults.class);
				this.envAgent.setCurrentResults(newVoteResults);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("DAY_STATE"))
		{
			this.envAgent.setDayState(message.getContent());
		}
		else if(message.getConversationId().equals("ACTION_LOG"))
		{
			this.envAgent.getActionLogs().add(message.getContent());
		}
		else if(message.getConversationId().equals("TURN"))
		{
			this.envAgent.setTurn(message.getContent());
		}
		else if(message.getConversationId().equals("END_GAME"))
		{
			this.envAgent.setEndGame(Boolean.parseBoolean(message.getContent()));
		}
	}

	

}
