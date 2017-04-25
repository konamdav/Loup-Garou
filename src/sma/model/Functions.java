package sma.model;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class Functions {
	public static void  newActionToLog(String action, Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(a.getAID());
		msg.setConversationId("ACTION_LOG");
		msg.setContent(action);
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			msg.addReceiver(agents.get(0));
			a.send(msg);
		}
	}
	
	public static void  updateDayState(String state, Agent a, int gameid)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(a.getAID());
		msg.setConversationId("DAY_STATE");
		msg.setContent(state);
		
		List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", a, gameid);
		if(!agents.isEmpty())
		{
			msg.addReceiver(agents.get(0));
			a.send(msg);
		}
	}
}
