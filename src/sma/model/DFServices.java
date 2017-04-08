package sma.model;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFServices {
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
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();

		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "GAME_"+gameid));
		dfad.addServices(sd);

		try {
			DFService.register(agent, dfad);
		}
		catch (FIPAException fe) {
			
		}
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
