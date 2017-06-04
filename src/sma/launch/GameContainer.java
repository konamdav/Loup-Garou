package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import sma.cupid_controller.CupidControllerAgent;
import sma.environment_agent.EnvironmentAgent;
import sma.family_controller.FamilyControllerAgent;
import sma.flute_player_controller.FlutePlayerControllerAgent;
import sma.great_werewolf_controller_agent.GreatWerewolfControllerAgent;
import sma.medium_controller.MediumControllerAgent;
import sma.model.GameSettings;
import sma.model.Roles;
import sma.voleur_controller.VoleurControllerAgent;
import sma.witch_controller.WitchControllerAgent;

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

			/** 2 agents d'environnements **/
			AgentController ac = container.createNewAgent(
					"ENVIRONMENT_AGENT_1_"+gameid, EnvironmentAgent.class.getName(), objects);
			ac.start();
			
			ac = container.createNewAgent(
					"ENVIRONMENT_AGENT_2_"+gameid, EnvironmentAgent.class.getName(), objects);
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
			
			//selection role great werewolf
			if(this.gameSettings.isRoleRegistered(Roles.GREAT_WEREWOLF))
			{
				ac = container.createNewAgent(
						"GREAT_WEREWOLF_CONTROLLER_AGENT_"+gameid, GreatWerewolfControllerAgent.class.getName(), objects);
				ac.start();
			}
			

			//selection role witch
			if(this.gameSettings.isRoleRegistered(Roles.WITCH))
			{
				ac = container.createNewAgent(
						"WITCH_CONTROLLER_AGENT_"+gameid, WitchControllerAgent.class.getName(), objects);
				ac.start();
			}
			
			//selection role FAMILY
			if(this.gameSettings.isRoleRegistered(Roles.FAMILY))
			{
				ac = container.createNewAgent(
						"FAMILY_CONTROLLER_AGENT_"+gameid, FamilyControllerAgent.class.getName(), objects);
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
			
			//selection role voleur player
			if(this.gameSettings.isRoleRegistered(Roles.VOLEUR))
			{
				ac = container.createNewAgent(
						"VOLEUR_CONTROLLER_AGENT_"+gameid, VoleurControllerAgent.class.getName(), objects);
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
