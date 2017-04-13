package sma.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFServices {
	private static HashMap<AID, DFAgentDescription> registered = new HashMap<AID, DFAgentDescription>();
	
	public static void registerSystemAgent(String type, String name, Agent agent){
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();

		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "SYSTEM"));
		dfad.addServices(sd);

		try {
			DFService.register(agent, dfad);
		}
		catch (FIPAException fe) {
			
		}
	}
	
	public static void registerGameAgent(String type, String name, Agent agent,  int gameid){
		
		ServiceDescription sd = new ServiceDescription();

		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "GAME_"+gameid));
		
		try {
			if(registered.containsKey(agent.getAID()))
			{
				DFAgentDescription dfad = registered.get(agent.getAID());
				dfad.addServices(sd);
				
				DFService.modify(agent, dfad);
			}
			else
			{
				DFAgentDescription dfad = new DFAgentDescription();
				dfad.setName(agent.getAID());
				dfad.addServices(sd);
				
				DFService.register(agent, dfad);
				registered.put(agent.getAID(), dfad);
			}
			
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	public static void deregisterGameAgent(String type, String name, Agent agent,  int gameid){
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "GAME_"+gameid));
		
		DFAgentDescription dfad = null;
		if(registered.containsKey(agent.getAID()))
		{
			dfad = registered.get(agent.getAID());
			dfad.removeServices(sd);
			
			try {
				DFService.modify(agent, dfad);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		

		
	}

	
	public static void setStatusAgent(String status, Agent agent,  int gameid)
	{
		DFServices.deregisterGameAgent("PLAYER", "SLEEP", agent, gameid);
		DFServices.deregisterGameAgent("PLAYER", "WAKE", agent, gameid);
		DFServices.deregisterGameAgent("PLAYER", "DEAD", agent, gameid);
		
		DFServices.registerGameAgent("PLAYER", status, agent, gameid);
	}

	public static List<AID> findGameAgent(String type, String name, Agent agent, int gameid){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "GAME_"+gameid));
		
		template.addServices(sd);

		ArrayList<AID> agents = new ArrayList<AID>();
		try {
			DFAgentDescription[] result =
					DFService.search(agent, template);
			if (result.length > 0){
				for(DFAgentDescription agentDescr : result)
				{
					agents.add(agentDescr.getName());
				}
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return agents;
	}
	
	public static List<AID> findSystemAgent(String type, String name, Agent agent){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		
		sd.addProperties(new Property("CONTAINER", "SYSTEM"));
		template.addServices(sd);

		ArrayList<AID> agents = new ArrayList<AID>();
		try {
			DFAgentDescription[] result =
					DFService.search(agent, template);
			if (result.length > 0){
				for(DFAgentDescription agentDescr : result)
				{
					agents.add(agentDescr.getName());
				}
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return agents;
	}

}
