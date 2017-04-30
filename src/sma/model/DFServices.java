package sma.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sma.data.Data;

public class DFServices {
	private static HashMap<AID, DFAgentDescription> registered = new HashMap<AID, DFAgentDescription>();
	private static Comparator<AID> comparator = new Comparator<AID>() {
		@Override
		public int compare(AID a1, AID a2)
		{
			return  a1.getLocalName().compareTo(a2.getLocalName());
		}
	};

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

	public static void registerPlayerAgent(String name, Agent agent,  int gameid){
		DFServices.registerGameAgent("PLAYER", name, agent, gameid);
	}

	public static void registerPlayerAgent(ArrayList<String> names, Agent agent,  int gameid){
		for(String name  : names)
		{
			DFServices.registerPlayerAgent(name, agent, gameid);
		}
	}

	public static void modifyPlayerAgent(String old_name, String new_name, Agent agent,  int gameid){
		//TODO Mayebe do a proper way, find this agent, and obtain this dfd and modifiy it

		DFServices.deregisterGameAgent("PLAYER", old_name, agent, gameid);
		DFServices.registerGameAgent("PLAYER", new_name, agent, gameid);
	}

	public static void deregisterPlayerAgent(String name, Agent agent,  int gameid){
		DFServices.deregisterGameAgent("PLAYER", name, agent, gameid);
	}

	public static void deregisterPlayerAgent(ArrayList<String> names, Agent agent,  int gameid){
		for(String name  : names)
		{
			DFServices.deregisterPlayerAgent(name, agent, gameid);
		}
	}

	public static void registerGameControllerAgent(String name, Agent agent,  int gameid){
		DFServices.registerGameAgent("CONTROLLER", name, agent, gameid);
	}

	private static void registerGameAgent(String type, String name, Agent agent,  int gameid){

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

	private static void deregisterGameAgent(String type, String name, Agent agent,  int gameid){

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


	public static void setStatusPlayerAgent(String status, Agent agent,  int gameid)
	{
		DFServices.deregisterGameAgent("PLAYER", "SLEEP", agent, gameid);
		DFServices.deregisterGameAgent("PLAYER", "WAKE", agent, gameid);
		DFServices.deregisterGameAgent("PLAYER", "DEAD", agent, gameid);

		DFServices.registerGameAgent("PLAYER", status, agent, gameid);
	}

	public static boolean containsGameAgent(AID agent,String type, String name, Agent searcher, int gameid){
		return DFServices.findGameAgent(type, name, searcher, gameid).contains(agent);
	}

	public static List<AID> findGamePlayerAgent(String name, Agent agent, int gameid){
		return DFServices.findGameAgent("PLAYER", name, agent, gameid);
	}

	/** recupere les voisins **/
	public static List<AID> findNeighbors(AID player, Agent agent, int gameid)
	{
		List<AID> res = new ArrayList<AID>();
		List<AID> citizens = findOrderedCitizen(agent, gameid);
		
		int i = 0; 
		boolean flag = false;
		while(i<citizens.size() && !flag)
		{
			AID aid = citizens.get(i);
			if(aid.getLocalName().equals(player.getLocalName()))
			{
				int area = Math.min(citizens.size()-1, Data.AREA_NEIGHBORS);
				
				/** recuperation des voisins de portée N **/
				for(int j = i-1; j<= i-area; j--)
				{
					int index = j;
					if(j<0)
					{
						index = citizens.size()+j;
					}
					res.add(citizens.get(index));
				}
				
				for(int j = i+1; j<= i+area; j++)
				{
					int index = j;
					if(j>=citizens.size())
					{
						index = j%citizens.size();
					}
					res.add(citizens.get(index));
				}
				flag = true;
			}
			++i;
		}
		
		return res;
	}
	
	/** recupere les voisins d'un coté **/
	public static List<AID> findNeighborsBySide(String side, AID player, Agent agent, int gameid)
	{
		List<AID> tmp = findNeighbors(player, agent, gameid);
		List<AID> res = new ArrayList<AID>();
		if(side.equals("LEFT"))
		{
			for(int i = 0; i<Data.AREA_NEIGHBORS; ++i)
			{
				res.add(tmp.get(i));
			}
		}
		else
		{
			for(int i = Data.AREA_NEIGHBORS; i<2*Data.AREA_NEIGHBORS; ++i)
			{
				res.add(tmp.get(i));
			}
		}
		
		return res;
	}
	
	/** trouver les voisins **/
	public static List<AID> findOrderedCitizen(Agent agent, int gameid)
	{
		String[] services1 = {Roles.CITIZEN, Status.WAKE};
		String[] services2 = {Roles.CITIZEN, Status.SLEEP};

		List<AID> citizens = DFServices.findGamePlayerAgent(services1, agent, gameid);
		List<AID> tmp = DFServices.findGamePlayerAgent(services1, agent, gameid);

		citizens.addAll(tmp);
		Collections.sort(citizens, comparator);

		return citizens;
	}

	public static List<AID> findGameControllerAgent(String name, Agent agent, int gameid){
		return DFServices.findGameAgent("CONTROLLER", name, agent, gameid);
	}

	public static List<AID> findGamePlayerAgent(String[] names, Agent agent, int gameid){
		List<AID> tmp = new ArrayList<AID>();
		boolean flag = false;

		for(String name : names)
		{
			if(!flag)
			{
				flag = true;
				tmp.addAll(DFServices.findGamePlayerAgent(name, agent, gameid));
			}
			else
			{
				tmp.retainAll(DFServices.findGamePlayerAgent(name, agent, gameid));
			}
		}

		return tmp;
	}

	private static List<AID> findGameAgent(String type, String name, Agent agent, int gameid){
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


	public static List<PlayerProfile> getPlayerProfiles(Agent agent, int gameid)
	{
		HashMap<String, PlayerProfile> tmp = new HashMap<String, PlayerProfile>();

		//get profiles joueurs citizen
		List<AID> citizens = DFServices.findGamePlayerAgent("CITIZEN", agent, gameid);

		for(AID citizen : citizens)
		{
			PlayerProfile profile = new PlayerProfile();
			profile.setName(citizen.getLocalName());
			profile.getRoles().add("CITIZEN");
			tmp.put(profile.getName(), profile);
		}

		//get profiles joueurs werewolf
		List<AID> werewolves = DFServices.findGamePlayerAgent("WEREWOLF", agent, gameid);

		for(AID werewolf : werewolves)
		{
			PlayerProfile profile = tmp.get(werewolf.getLocalName());
			profile.getRoles().add("WEREWOLF");
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs wake
		List<AID> wakes = DFServices.findGamePlayerAgent("WAKE", agent, gameid);

		for(AID wake : wakes)
		{
			PlayerProfile profile = tmp.get(wake.getLocalName());
			profile.setStatus("WAKE");
		}

		//get profiles joueurs sleep
		List<AID> sleeps = DFServices.findGamePlayerAgent("SLEEP", agent, gameid);

		for(AID sleep : sleeps)
		{
			PlayerProfile profile = tmp.get(sleep.getLocalName());
			profile.setStatus("SLEEP");
		}

		//get profiles joueurs dead
		List<AID> deads = DFServices.findGamePlayerAgent("DEAD", agent, gameid);

		for(AID dead : deads)
		{
			PlayerProfile profile = tmp.get(dead.getLocalName());
			profile.setStatus("DEAD");
		}

		List<PlayerProfile> list = new ArrayList<PlayerProfile>();
		for(Entry<String, PlayerProfile> entry : tmp.entrySet())
		{
			list.add(entry.getValue());

			entry.getValue().print();
		}

		return list;
	}
}
