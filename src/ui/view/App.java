package ui.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import jade.core.Agent;
import sma.launch.GameContainer;
import sma.launch.SystemContainer;
import sma.model.GameInformations;
import sma.model.PlayerProfile;
import sma.model.VoteResults;
import ui.agent.uiAgent;
import ui.sma.UIContainer;

/***
 * Moteur graphique 
 */
public class App extends Game {
	
	private LwjglApplicationConfiguration config;
	
	public SystemContainer systemContainer = null; 
	
	public List<Integer> containers = null;

	public UIContainer uiContainer = null; 
	
	public uiAgent agent = null ;
	
	public GameInformations gameInformations;

	public GameInformations getGameInformations() {
		return gameInformations;
	}

	public void setGameInformations(GameInformations gameInformations) {
		this.gameInformations = gameInformations;
	}

	public List<Integer> getContainers() {
		return containers;
	}

	public void setContainers(List<Integer> containers2) {
		this.containers = containers2;
	}



	public SystemContainer newSystemContainer() {
		systemContainer = new SystemContainer(this);
		return systemContainer;
	}
	
	public SystemContainer getSystemContainer() {
		return systemContainer;
		
		
	}
	

	public UIContainer newUIContainer(String ip) {
		uiContainer = new UIContainer(ip, this);
		return uiContainer;
	}
	
	public UIContainer getUIContainerr() {
		return uiContainer;
	}


	public App(LwjglApplicationConfiguration config) {
		this.config=config;
	}
	
	public App(LwjglApplicationConfiguration config, uiAgent a) {
		this.config=config;
		this.agent = a;
	}

	public LwjglApplicationConfiguration getConfig() {
		return config;
	}

	public void create() {
		//agent = new uiAgent();

		//newSystemContainer();
		gameInformations = new GameInformations();
		this.setScreen(new ViewMainMenu(this));
	}
	
	public void setAgent(uiAgent a){
		agent = a; 
	}
	
}
