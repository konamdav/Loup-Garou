package ui.view;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import jade.core.Agent;
import sma.launch.GameContainer;
import sma.launch.SystemContainer;
import ui.agent.uiAgent;
import ui.sma.UIContainer;

/***
 * Moteur graphique 
 */
public class App extends Game {
	private LwjglApplicationConfiguration config;
	
	public SystemContainer systemContainer = null; 
	
	public List<GameContainer> containers = null;

	public List<GameContainer> getContainers() {
		return containers;
	}

	public void setContainers(List<GameContainer> containers) {
		this.containers = containers;
	}

	public UIContainer uiContainer = null; 
	
	public uiAgent agent = null ;


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
		this.setScreen(new ViewMainMenu(this));
/*
		newSystemContainer();
		this.setScreen(new ViewInterfaceGame(this));
*/
	}
	
	public void setAgent(uiAgent a){
		agent = a; 
	}
	
}
