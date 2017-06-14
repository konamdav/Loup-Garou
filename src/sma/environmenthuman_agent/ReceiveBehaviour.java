package sma.environmenthuman_agent;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import sma.model.HumanVoteRequest;
import sma.model.VoteResults;

public class ReceiveBehaviour extends OneShotBehaviour {

	private EnvironmentHumanAgent envAgent;
	private ACLMessage message;


	public ReceiveBehaviour(EnvironmentHumanAgent envAgent, ACLMessage message) {
		super();
		this.envAgent = envAgent;
		this.message = message;
	}


	@Override
	public void action() {

		//System.out.println("RECEIVE DATA");

		ObjectMapper mapper = new ObjectMapper();

		if(message.getConversationId().equals("NEW_CITIZEN_VOTE_RESULTS"))
		{
			try {
				VoteResults newVoteResults = mapper.readValue(message.getContent(), VoteResults.class);

				this.envAgent.setCurrentResults(newVoteResults);
				this.envAgent.getGlobalResults().add(newVoteResults);

//				System.out.println("ENV AGENT | MAJ RESULTS ");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("NEW_VOTE_RESULTS"))
		{
			if(!this.envAgent.isGame_mode() || (this.envAgent.isGame_mode() && this.envAgent.getCptHuman()>0))
			{
				try {
					VoteResults newVoteResults = mapper.readValue(message.getContent(), VoteResults.class);
					this.envAgent.setCurrentResults(newVoteResults);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if(message.getConversationId().equals("DAY_STATE"))
		{
			this.envAgent.setDayState(message.getContent());
		}
		else if(message.getConversationId().equals("ACTION_LOG"))
		{
			//System.out.println("RECV ACTION");
			if(!this.envAgent.isGame_mode() || (this.envAgent.isGame_mode() && this.envAgent.getCptHuman()>0))
			{
				this.envAgent.getActionLogs().add(message.getContent());
			}
		}
		else if(message.getConversationId().equals("ACTION_LOG_IMPORTANT"))
		{
			//System.out.println("RECV ACTION");
			this.envAgent.getActionLogs().add(message.getContent());

		}
		else if(message.getConversationId().equals("NUM_TURN"))
		{
			this.envAgent.setNum_turn(Integer.parseInt(message.getContent()));
		}
		else if(message.getConversationId().equals("TURN"))
		{
			//System.out.println("TURN => "+message.getContent());
			this.envAgent.setTurn(message.getContent());
		}
		else if(message.getConversationId().equals("END_GAME"))
		{
			this.envAgent.setEndGame(Boolean.parseBoolean(message.getContent()));
		}
		else if(message.getConversationId().equals("INC_HUMANS"))
		{
			if(message.getSender().equals(this.envAgent.getPlayer()))
			{
				this.envAgent.setCptHuman(this.envAgent.getCptHuman()+1);
			}
		}
		else if(message.getConversationId().equals("DEC_HUMANS"))
		{
			if(message.getSender().equals(this.envAgent.getPlayer()))
			{
				this.envAgent.setCptHuman(this.envAgent.getCptHuman()-1);
			}
		}
		else if(message.getConversationId().equals("HUMAN_VOTE_REQUEST"))
		{
			if(message.getSender().equals(this.envAgent.getPlayer()))
			{
				try {
					HumanVoteRequest req = mapper.readValue(message.getContent(), HumanVoteRequest.class);
					if(req == null )
					{
						if(!this.envAgent.getStackRequest().isEmpty())
						{
							req = this.envAgent.getStackRequest().pop();
						}

						this.envAgent.setHumanVoteRequest(req);

					}
					else
					{
						if(this.envAgent.getStackRequest().isEmpty())
						{
							this.envAgent.setHumanVoteRequest(req);
						}
						else
						{
							this.envAgent.getStackRequest().push(req);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



}
