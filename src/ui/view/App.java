package ui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import jade.core.Agent;
import sma.launch.SystemContainer;
import ui.agent.uiAgent;
import ui.sma.UIContainer;

/***
 * Moteur graphique 
 */
public class App extends Game {
	private LwjglApplicationConfiguration config;
	
	public SystemContainer systemContainer = null; 

	public UIContainer uiContainer = null; 
	
	public uiAgent agent = null ;
	

	public SystemContainer newSystemContainer() {
		systemContainer = new SystemContainer();
		return systemContainer;
	}
	
	public SystemContainer getSystemContainer() {
		return systemContainer;
	}
	

	public UIContainer newUIContainer() {
		uiContainer = new UIContainer("127.0.0.1");
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
		this.setScreen(new ViewMainMenu(this));
	}
	
	
	
}
