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
import jade.util.leap.Iterator;
import sma.data.Data;

public class DFServices {
	private static HashMap<AID, DFAgentDescription> registered = new HashMap<AID, DFAgentDescription>();
	private static Comparator<AID> comparator = new Comparator<AID>() {
		@Override
		public int compare(AID a1, AID a2)
		{
			return  a1.getLocalName().hashCode()>a2.getLocalName().hashCode()?0:1;
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

	public static void registerSystemControllerAgent(Agent agent){
		DFServices.registerSystemAgent("SYSTEM", "CONTROLLER", agent);
	}

	public static void registerGameControllerAgent(String name, Agent agent,  int gameid){
		DFServices.registerGameAgent("CONTROLLER", name, agent, gameid);
	}


	private static DFAgentDescription getDFAgentDescription(Agent agent)
	{
		DFAgentDescription descriptionAgent = null;
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(agent.getAID());
		try 
		{

			DFAgentDescription[] dfds = DFService.search(agent, dfad);
			if(dfds.length > 0)
			{
				descriptionAgent = dfds[0];
			}
			else
			{
				descriptionAgent = dfad;
			}

		} 
		catch (FIPAException e) 
		{
			descriptionAgent = dfad;
			e.printStackTrace();
		}

		return descriptionAgent;
	}

	private static void registerGameAgent(String type, String name, Agent agent,  int gameid)
	{

		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		sd.addProperties(new Property("CONTAINER", "GAME_"+gameid));

		try {
			DFAgentDescription dfad = getDFAgentDescription(agent);

			if(!dfad.getAllServices().hasNext())
			{
				dfad.addServices(sd);
				DFService.register(agent, dfad);
			}
			else
			{
				dfad.addServices(sd);
				DFService.modify(agent, dfad);
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}



	private static void deregisterGameAgent(String type, String name, Agent agent,  int gameid){
		DFAgentDescription dfad = getDFAgentDescription(agent);

		Iterator it = dfad.getAllServices();
		boolean flag = false;
		ServiceDescription sd = null;
		while(it.hasNext() && !flag )
		{
			sd = (ServiceDescription) it.next();
			if(sd.getName().equals(name) && sd.getType().equals(type))
			{
				flag = true;
			}

		}

		if(flag)
		{
			dfad.removeServices(sd);
			try {
				DFService.modify(agent, dfad);
			} catch (FIPAException e) {
				e.printStackTrace();
			}	
		}
	}



	public static void setStatusPlayerAgent(String status, Agent agent,  int gameid)
	{
		DFServices.deregisterGameAgent("PLAYER", Status.SLEEP, agent, gameid);
		DFServices.deregisterGameAgent("PLAYER", Status.WAKE, agent, gameid);

		DFServices.deregisterGameAgent("PLAYER", Status.DEAD, agent, gameid); //TODO cedric decooment + test	

		DFServices.registerGameAgent("PLAYER", status, agent, gameid);
	}

	public static boolean containsGameAgent(AID agent,String type, String name, Agent searcher, int gameid){
		return DFServices.findGameAgent(type, name, searcher, gameid).contains(agent);
	}

	public static AID getSystemController(Agent agent) {
		List<AID> list = DFServices.findSystemAgent("SYSTEM", "CONTROLLER", agent);
		if(list.isEmpty())
		{
			return null;
		}
		
		return list.get(0);
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
			if(aid.getName().equals(player.getName()))
			{
				int area = Math.min(citizens.size()-1, Data.AREA_NEIGHBORS);

				/** recuperation des voisins de portée N **/
				for(int j = i-area; j<= i-1; j++)
				{
					int index = j;
					if(j<0)
					{
						index = citizens.size()+j;
					}
					if(!res.contains(citizens.get(index)) && !citizens.get(index).getName().equals(player.getName()))
					{
						res.add(citizens.get(index));
					}
				}

				for(int j = i+1; j<= i+area; j++)
				{
					int index = j;
					if(j>=citizens.size())
					{
						index = j%citizens.size();
					}

					if(!res.contains(citizens.get(index)) && !citizens.get(index).getName().equals(player.getName()))
					{
						res.add(citizens.get(index));
					}
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
			for(int i = 0; i<Math.min(tmp.size(),Data.AREA_NEIGHBORS); ++i)
			{
				if(!res.contains(tmp.get(i)))
				{
					res.add(tmp.get(i));
				}
			}
		}
		else
		{
			int index = Math.min(tmp.size(),Data.AREA_NEIGHBORS);
			for(int i = Math.min(tmp.size(),Data.AREA_NEIGHBORS); i< Data.AREA_NEIGHBORS+Math.min(tmp.size(),Data.AREA_NEIGHBORS); ++i)
			{
				if(index >= tmp.size())
				{
					index = 0;
				}

				if(!res.contains(tmp.get(index)))
				{
					res.add(tmp.get(index));
				}

				++index;
			}

		}

		return res;
	}

	/** recupere les voisins d'un coté **/
	public static List<AID> findNeighborsBySide2(String side, AID player, Agent agent, int gameid)
	{
		List<AID> tmp = findNeighbors(player, agent, gameid);
		List<AID> res = new ArrayList<AID>();
		if(side.equals("LEFT"))
		{
			for(int i = 0; i<Math.min(tmp.size(),Data.AREA_NEIGHBORS); ++i)
			{
				if(!res.contains(tmp.get(i)))
				{
					res.add(tmp.get(i));
				}
			}
		}
		else
		{
			int reste = tmp.size() - Math.min(tmp.size(),Data.AREA_NEIGHBORS);
			if(reste > 0){
				for(int i = Data.AREA_NEIGHBORS; i< Data.AREA_NEIGHBORS+reste; ++i)
				{
					if(!res.contains(tmp.get(i)))
					{
						res.add(tmp.get(i));
					}
				}
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
		List<AID> tmp = DFServices.findGamePlayerAgent(services2, agent, gameid);

		citizens.addAll(tmp);
		Collections.sort(citizens, comparator);

		return citizens;
	}


	/** trouver les voisins **/
	public static List<AID> findOrderedAllCitizen(Agent agent, int gameid)
	{
		String[] services1 = {Roles.CITIZEN};

		List<AID> citizens = DFServices.findGamePlayerAgent(services1, agent, gameid);
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

	public static List<PlayerProfile> getPlayerProfiles(Agent agent, String player, int gameid)
	{
		return DFServices.getPlayerProfiles(false, 0, player, agent, gameid);
	}

	public static List<PlayerProfile> getPlayerProfiles(Agent agent, int gameid)
	{
		return DFServices.getPlayerProfiles(false, 0, "", agent, gameid);
	}

	public static List<PlayerProfile> getPlayerProfiles(boolean game_mode, int cptHuman, String player, Agent agent, int gameid)
	{
		HashMap<String, PlayerProfile> tmp = new HashMap<String, PlayerProfile>();

		//get profiles joueurs citizen
		List<AID> citizens = DFServices.findOrderedAllCitizen(agent, gameid);
		//System.out.println(citizens);
		for(int i =0; i<citizens.size(); ++i)
		{
			PlayerProfile profile = new PlayerProfile();
			profile.setName(citizens.get(i).getLocalName());
			profile.getRoles().add("CITIZEN");
			tmp.put(profile.getName(), profile);
		}

		//get profiles joueurs 
		List<AID> humans = DFServices.findGamePlayerAgent("HUMAN", agent, gameid);
		for(AID human : humans)
		{
			PlayerProfile profile = tmp.get(human.getLocalName());
			profile.getRoles().add("HUMAN");

		}

		//get profiles joueurs werewolf
		List<AID> werewolves = DFServices.findGamePlayerAgent("WEREWOLF", agent, gameid);

		for(AID werewolf : werewolves)
		{
			PlayerProfile profile = tmp.get(werewolf.getLocalName());
			profile.getRoles().add("WEREWOLF");
			profile.getRoles().remove("CITIZEN");
		}


		//get profiles joueurs angel
		List<AID> angels = DFServices.findGamePlayerAgent(Roles.ANGEL, agent, gameid);
		for(AID angel : angels)
		{
			PlayerProfile profile = tmp.get(angel.getLocalName());
			profile.getRoles().add("ANGEL");
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs flute
		List<AID> flutes = DFServices.findGamePlayerAgent(Roles.FLUTE_PLAYER, agent, gameid);
		for(AID flute : flutes)
		{
			PlayerProfile profile = tmp.get(flute.getLocalName());
			profile.getRoles().add(Roles.FLUTE_PLAYER);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs 
		List<AID> hunters = DFServices.findGamePlayerAgent(Roles.HUNTER, agent, gameid);
		for(AID hunter : hunters)
		{
			PlayerProfile profile = tmp.get(hunter.getLocalName());
			profile.getRoles().add(Roles.HUNTER);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs flute
		List<AID> girls = DFServices.findGamePlayerAgent(Roles.LITTLE_GIRL, agent, gameid);
		for(AID girl : girls)
		{
			PlayerProfile profile = tmp.get(girl.getLocalName());
			profile.getRoles().add(Roles.LITTLE_GIRL);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs witch
		List<AID> witches = DFServices.findGamePlayerAgent(Roles.WITCH, agent, gameid);
		for(AID witch : witches)
		{
			PlayerProfile profile = tmp.get(witch.getLocalName());
			profile.getRoles().add(Roles.WITCH);
			profile.getRoles().remove("CITIZEN");
		}


		//get profiles joueurs flute
		List<AID> cupids = DFServices.findGamePlayerAgent(Roles.CUPID, agent, gameid);
		for(AID cupid : cupids)
		{
			PlayerProfile profile = tmp.get(cupid.getLocalName());
			profile.getRoles().add(Roles.CUPID);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs familiy
		List<AID> familys = DFServices.findGamePlayerAgent(Roles.FAMILY, agent, gameid);
		for(AID family : familys)
		{
			PlayerProfile profile = tmp.get(family.getLocalName());
			profile.getRoles().add(Roles.FAMILY);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs exorcists
		List<AID> exorcists = DFServices.findGamePlayerAgent(Roles.EXORCIST, agent, gameid);
		for(AID exorcist : exorcists)
		{
			PlayerProfile profile = tmp.get(exorcist.getLocalName());
			profile.getRoles().add(Roles.EXORCIST);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs Voleur
		List<AID> voleurs = DFServices.findGamePlayerAgent(Roles.THIEF, agent, gameid);
		for(AID voleur : voleurs)
		{
			PlayerProfile profile = tmp.get(voleur.getLocalName());
			profile.getRoles().add(Roles.THIEF);
			profile.getRoles().remove("CITIZEN");
		}

		//get profiles joueurs 
		List<AID> great_werewolves = DFServices.findGamePlayerAgent(Roles.GREAT_WEREWOLF, agent, gameid);
		for(AID werewolf : great_werewolves)
		{
			PlayerProfile profile = tmp.get(werewolf.getLocalName());
			profile.getRoles().add(Roles.GREAT_WEREWOLF);
			profile.getRoles().remove(Roles.WEREWOLF);
			profile.getRoles().remove(Roles.CITIZEN);
		}

		//get profiles joueurs loup blanc
		List<AID> white_werewolves = DFServices.findGamePlayerAgent(Roles.WHITE_WEREWOLF, agent, gameid);
		for(AID werewolf : white_werewolves)
		{
			PlayerProfile profile = tmp.get(werewolf.getLocalName());
			profile.getRoles().add(Roles.WHITE_WEREWOLF);
			profile.getRoles().remove(Roles.WEREWOLF);
			profile.getRoles().remove(Roles.CITIZEN);
		}

		//get profiles joueurs lover
		List<AID> lovers = DFServices.findGamePlayerAgent(Roles.LOVER, agent, gameid);
		for(AID lover : lovers)
		{
			PlayerProfile profile = tmp.get(lover.getLocalName());
			profile.getRoles().add(Roles.LOVER);
		}

		//get profiles joueurs charmed
		List<AID> charmed = DFServices.findGamePlayerAgent(Roles.CHARMED, agent, gameid);
		for(AID ch : charmed)
		{
			PlayerProfile profile = tmp.get(ch.getLocalName());
			profile.getRoles().add(Roles.CHARMED);
		}

		//get profiles joueurs medium
		List<AID> mediums = DFServices.findGamePlayerAgent(Roles.MEDIUM, agent, gameid);

		for(AID medium : mediums)
		{
			PlayerProfile profile = tmp.get(medium.getLocalName());
			profile.getRoles().add(Roles.MEDIUM);
			profile.getRoles().remove(Roles.CITIZEN);
		}

		//get profiles joueurs salvators
		List<AID> salvators = DFServices.findGamePlayerAgent(Roles.SALVATOR, agent, gameid);

		for(AID salvator : salvators)
		{
			PlayerProfile profile = tmp.get(salvator.getLocalName());
			profile.getRoles().add(Roles.SALVATOR);
			profile.getRoles().remove(Roles.CITIZEN);
		}

		//get profiles joueurs ancien
		List<AID> anciens = DFServices.findGamePlayerAgent(Roles.ANCIENT, agent, gameid);

		for(AID ancien : anciens)
		{
			PlayerProfile profile = tmp.get(ancien.getLocalName());
			profile.getRoles().add(Roles.ANCIENT);
			profile.getRoles().remove(Roles.CITIZEN);
		}

		//get profiles joueurs wake
		List<AID> wakes = DFServices.findGamePlayerAgent("WAKE", agent, gameid);

		for(AID wake : wakes)
		{
			PlayerProfile profile = tmp.get(wake.getLocalName());
			if(!game_mode || (game_mode && cptHuman > 0) ){
				profile.setStatus("WAKE");
			}
			else
			{
				profile.setStatus("SLEEP");
			}
		}

		//get profiles joueurs sleep
		List<AID> sleeps = DFServices.findGamePlayerAgent("SLEEP", agent, gameid);

		for(AID sleep : sleeps)
		{
			PlayerProfile profile = tmp.get(sleep.getLocalName());
			//System.out.println("Juste befoure error " + sleep.getLocalName() + "   " + sleep.getName());
			profile.setStatus("SLEEP");
		}

		//get profiles joueurs dead
		List<AID> deads = DFServices.findGamePlayerAgent("DEAD", agent, gameid);

		for(AID dead : deads)
		{
			PlayerProfile profile = tmp.get(dead.getLocalName());
			profile.getRoles().remove("MAYOR");
			profile.setStatus("DEAD");
		}

		//** restriction champs de vision humain **/
		if(game_mode)
		{
			List<String> rolesHuman = new ArrayList<String>();
			for(AID human : humans)
			{
				if(player.isEmpty() || (!player.isEmpty() && human.getLocalName().equals(player)))
				{

					for(String s : tmp.get(human.getLocalName()).getRoles())
					{
						if(!rolesHuman.contains(s))
						{
							rolesHuman.add(s);
						}
					}
				}
			}

			//System.out.println(" player h "+player);

			for(AID citizen : citizens)
			{				
				if(!tmp.get(citizen.getLocalName()).getStatus().equals(Status.DEAD) 
						&& ((!player.isEmpty() && !citizen.getLocalName().equals(player))||(player.isEmpty() && !tmp.get(citizen.getLocalName()).getRoles().contains("HUMAN")))){
					List<String> roles = new ArrayList<String>();
					for(String s : tmp.get(citizen.getLocalName()).getRoles())
					{
						if(rolesHuman.contains(s) 
								&&!s.equals(Roles.CITIZEN)
								&&!s.equals(Roles.ANGEL)
								&&!s.equals(Roles.WITCH)
								&&!s.equals(Roles.SALVATOR)
								&&!s.equals(Roles.THIEF)
								&&!s.equals(Roles.HUNTER)
								&&!s.equals(Roles.LITTLE_GIRL)
								&&!s.equals(Roles.MEDIUM)
								&&!s.equals(Roles.SCAPEGOAT) 
								|| (s.equals(Roles.CHARMED) && rolesHuman.contains(Roles.FLUTE_PLAYER) )
								|| (s.equals(Roles.FLUTE_PLAYER) && rolesHuman.contains(Roles.CHARMED) )
								|| (s.equals(Roles.WEREWOLF) && rolesHuman.contains(Roles.GREAT_WEREWOLF) )
								|| (s.equals(Roles.WEREWOLF) && rolesHuman.contains(Roles.WHITE_WEREWOLF)) 
								|| (s.equals(Roles.WHITE_WEREWOLF) && rolesHuman.contains(Roles.WEREWOLF))
								|| (s.equals(Roles.GREAT_WEREWOLF) && rolesHuman.contains(Roles.WEREWOLF)))

						{
							if((s.equals(Roles.WHITE_WEREWOLF) && rolesHuman.contains(Roles.WEREWOLF))||
									(s.equals(Roles.GREAT_WEREWOLF) && rolesHuman.contains(Roles.WEREWOLF))){

								roles.add(Roles.WEREWOLF);

							}
							else
							{
								roles.add(s);
							}
						}
					}
					tmp.get(citizen.getLocalName()).setRoles(roles);
				}

			}

		}
		//get profiles joueurs mayor
		List<AID> mayors = DFServices.findGamePlayerAgent(Roles.MAYOR, agent, gameid);
		for(AID mayor : mayors)
		{
			PlayerProfile profile = tmp.get(mayor.getLocalName());
			if(!profile.getStatus().equals(Status.DEAD)){
				profile.getRoles().add(Roles.MAYOR);
			}
		}

		//get profiles joueurs victims
		List<AID> victims = DFServices.findGamePlayerAgent("VICTIM", agent, gameid);

		if(!game_mode || (game_mode && cptHuman > 0) ){
			for(AID victim : victims)
			{
				PlayerProfile profile = tmp.get(victim.getLocalName());
				profile.getRoles().add("VICTIM");
			}
		}



		List<PlayerProfile> list = new ArrayList<PlayerProfile>();
		for(AID citizen : citizens)
		{
			list.add(tmp.get(citizen.getLocalName()));
			//entry.getValue().print();
		}

		return list;
	}

	public static void printProfiles(Agent agent, int gameid)
	{

		System.err.println(DFServices.findOrderedAllCitizen(agent, gameid));
		List<PlayerProfile> list = getPlayerProfiles(agent, gameid);
		for(int i = 0; i<list.size(); ++i)
		{
			list.get(i).print();
		}
	}
}
