package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import sma.cupid_controller.CupidControllerAgent;
import sma.flute_player_controller.FlutePlayerControllerAgent;
import sma.medium_controller.MediumControllerAgent;
import sma.model.GameSettings;
import sma.model.Roles;

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

			//selection role citizen
			if(this.gameSettings.isRoleRegistered(Roles.CITIZEN))
			{
				ac = container.createNewAgent(
						"CITIZEN_CONTROLLER_AGENT_"+gameid, "sma.citizen_controller_agent.CitizenControllerAgent", objects);
				ac.start();
			}

			//selection role werewolf
			if(this.gameSettings.isRoleRegistered(Roles.WEREWOLF))
			{
				ac = container.createNewAgent(
						"WEREWOLF_CONTROLLER_AGENT_"+gameid, "sma.werewolf_controller_agent.WerewolfControllerAgent", objects);
				ac.start();
			}
			
			//selection role CUPID
			if(this.gameSettings.isRoleRegistered(Roles.CUPID))
			{
				ac = container.createNewAgent(
						"CUPID_CONTROLLER_AGENT_"+gameid, CupidControllerAgent.class.getName(), objects);
				ac.start();
			}

			//selection role medium
			if(this.gameSettings.isRoleRegistered(Roles.MEDIUM))
			{
				ac = container.createNewAgent(
						"MEDIUM_CONTROLLER_AGENT_"+gameid, MediumControllerAgent.class.getName(), objects);
				ac.start();

			}
			
			//selection role flute player
			if(this.gameSettings.isRoleRegistered(Roles.FLUTE_PLAYER))
			{
				ac = container.createNewAgent(
						"FLUTE_PLAYER_CONTROLLER_AGENT_"+gameid, FlutePlayerControllerAgent.class.getName(), objects);
				ac.start();

			}
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
