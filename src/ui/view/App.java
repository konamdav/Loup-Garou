package ui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import sma.launch.SystemContainer;

/***
 * Moteur graphique 
 */
public class App extends Game {
	private LwjglApplicationConfiguration config;
	
	public SystemContainer systemContainer = null; 
	

	public SystemContainer newSystemContainer() {
		systemContainer = new SystemContainer();
		return systemContainer;
	}
	
	public SystemContainer getSystemContainer() {
		return systemContainer;
	}

	public App(LwjglApplicationConfiguration config) {
		this.config=config;
	}

	public LwjglApplicationConfiguration getConfig() {
		return config;
	}

	public void create() {
		
		this.setScreen(new ViewInterfaceGame(this));
	}
	
	
	
}
