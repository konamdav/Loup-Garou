package sma.model;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class Functions {
	public static void  newActionToLog(String action, Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
		msg.setSender(a.getAID());
		msg.setConversationId("ACTION_LOG");
		msg.setContent(action);
		
		System.err.println(action);
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			
			for(AID aid : agents)
			{
				msg.addReceiver(aid);
			}
			
			a.send(msg);
		}
	}
	
	public static void  updateDayState(String state, Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
		msg.setSender(a.getAID());
		msg.setConversationId("DAY_STATE");
		msg.setContent(state);
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			for(AID aid : agents)
			{
				msg.addReceiver(aid);
			}
			
			a.send(msg);
		}
	}
	
	public static void  updateTurn(String turn, Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
		msg.setSender(a.getAID());
		msg.setConversationId("TURN");
		msg.setContent(turn);
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			for(AID aid : agents)
			{
				msg.addReceiver(aid);
			}
			a.send(msg);
		}
	}
	
	public static void  setEndGame(Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
		msg.setSender(a.getAID());
		msg.setConversationId("END_GAME");
		msg.setContent("true");
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			for(AID aid : agents)
			{
				msg.addReceiver(aid);
			}
			
			a.send(msg);
		}
	}
}
