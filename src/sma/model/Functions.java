package sma.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	
	  public  static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

	        // 1. Convert Map to List of Map
	        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

	        // 2. Sort list with Collections.sort(), provide a custom Comparator
	        //    Try switch the o1 o2 position for a different order
	        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
	            public int compare(Map.Entry<String, Integer> o1,
	                               Map.Entry<String, Integer> o2) {
	                return (o2.getValue()).compareTo(o1.getValue());
	            }
	        });

	        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	        for (Map.Entry<String, Integer> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        return sortedMap;
	    }
}
