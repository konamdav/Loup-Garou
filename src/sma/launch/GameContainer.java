package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import sma.model.GameSettings;

public class GameContainer {
	private int gameid;
	public static String MAIN_PROPERTIES_FILE = "resources/sma/gamecontainer.properties";
	private AgentContainer container;
	private GameSettings gameSettings;
	
	public GameContainer(int gameid)
	{
		this.gameid = gameid;
		this.gameSettings = new GameSettings();
		
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			
			p.setParameter("container-name", "game_container_"+gameid);
			container = rt.createAgentContainer(p);	
			
			Object[] objects = new Object[2];
			objects[0] = this.gameid;
			objects[1] = this.gameSettings;
			
			AgentController ac = container.createNewAgent(
					"GAME_CONTROLLER_AGENT_"+gameid, "sma.game_controller_agent.GameControllerAgent", objects);
			ac.start();

			for(int i = 0; i<2; ++i)
			{
				ac = container.createNewAgent(
						"WEREWOLF_AGENT_"+i, "sma.werewolf_agent.WerewolfAgent", objects);
				ac.start();
			}
			
			
			ac = container.createNewAgent(
					"ENVIRONMENT_AGENT", "sma.environment_agent.EnvironmentAgent", objects);
			ac.start();
			
			ac = container.createNewAgent(
					"CitizenControllerAgent", "sma.citizen_controller_agent.CitizenControllerAgent", objects);
			ac.start();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public int getGameid() {
		return gameid;
	}
	
	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public AgentContainer getContainer() {
		return container;
	}
	

}
