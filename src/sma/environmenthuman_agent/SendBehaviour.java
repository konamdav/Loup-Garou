package sma.environmenthuman_agent;

import java.io.IOException;

import sma.model.DFServices;
import sma.model.GameInformations;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendBehaviour extends OneShotBehaviour {

	private EnvironmentHumanAgent envAgent;
	private ACLMessage message;


	public SendBehaviour(EnvironmentHumanAgent envAgent, ACLMessage message) {
		super();
		this.envAgent = envAgent;
		this.message = message;
	}


	@Override
	public void action() {
		//System.out.println("SEND DATA");

		ACLMessage reply = message.createReply();
		ObjectMapper mapper = new ObjectMapper();
		String contentString = "";
		if(message.getConversationId().equals("GLOBAL_VOTE_RESULTS"))
		{
			try {
				contentString = mapper.writeValueAsString(this.envAgent.getGlobalResults());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("DAY_STATE"))
		{
			contentString = this.envAgent.getDayState();	
		}
		else if(message.getConversationId().equals("TURN"))
		{
			contentString = this.envAgent.getTurn();	
		}
		else if(message.getConversationId().equals("END_GAME"))
		{
			contentString = this.envAgent.isEndGame() ? "true" : "false" ;	
		}
		else if(message.getConversationId().equals("NUM_TURN"))
		{
			contentString = ""+this.envAgent.getNum_turn();	
		}
		else if(message.getConversationId().equals("ACTION_LOGS"))
		{
			try {
				contentString = mapper.writeValueAsString(this.envAgent.getActionLogs());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("GAME_INFORMATIONS" + envAgent.getGameid()))
		{
			System.out.println("SEND GAMEINFORMATIONS");

			GameInformations gi = new GameInformations();
			gi.setActionLogs(envAgent.getActionLogs());
			gi.setCurrentResults(envAgent.getCurrentResults());
			gi.setDayState(envAgent.getDayState());
			gi.setEndGame(envAgent.isEndGame());
			gi.setProfiles(DFServices.getPlayerProfiles(this.envAgent.isGame_mode(), this.envAgent.getCptHuman(), this.envAgent.getPlayer().getLocalName(), envAgent, envAgent.getGameid()));
			gi.setTurn(envAgent.getTurn());
			gi.setNum_turn(envAgent.getNum_turn());
			gi.setVote(envAgent.getHumanVoteRequest());
			
			try {
				contentString = mapper.writeValueAsString(gi);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		reply.setContent(contentString);
		this.envAgent.send(reply);
	}



}
