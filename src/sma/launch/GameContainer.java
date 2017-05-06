package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import sma.model.GameSettings;

//Conteneur de jeu
/***
 * Conteneur de jeu
 * @author Davy
 *
 */
public class GameContainer {
	private int gameid;
	public static String MAIN_PROPERTIES_FILE = "resources/sma/gamecontainer.properties";
	private AgentContainer container;
	private GameSettings gameSettings;
	
	public GameContainer(int gameid, GameSettings gameSettings)
	{
		this.gameid = gameid;
		this.gameSettings = gameSettings;
		
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
					"ENVIRONMENT_AGENT_"+gameid, "sma.environment_agent.EnvironmentAgent", objects);
			ac.start();
			
			ac = container.createNewAgent(
					"GAME_CONTROLLER_AGENT_"+gameid, "sma.game_controller_agent.GameControllerAgent", objects);
			ac.start();
	
			ac = container.createNewAgent(
					"CITIZEN_CONTROLLER_AGENT_"+gameid, "sma.citizen_controller_agent.CitizenControllerAgent", objects);
			ac.start();
			
			ac = container.createNewAgent(
					"WEREWOLF_CONTROLLER_AGENT_"+gameid, "sma.werewolf_controller_agent.WerewolfControllerAgent", objects);
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
