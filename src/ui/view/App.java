package ui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/***
 * Moteur graphique 
 */
public class App extends Game {
	private LwjglApplicationConfiguration config;
	private StyleResources style;
	
	public App(LwjglApplicationConfiguration config) {
		this.config=config;
	}

	public LwjglApplicationConfiguration getConfig() {
		return config;
	}


	public StyleResources getStyle() {
		return style;
	}

	public void create() {
		
		this.setScreen(new ViewInterfaceGame(this));
	}
	
	
	
}
