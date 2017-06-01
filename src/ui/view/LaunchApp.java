package ui.view;



import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LaunchApp {

	/**
	 * Main de l'application
	 * @param args Arguments de lancement optionnels
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Werewolf";
		config.resizable = true; //on ne veut pas que l'utilisateur la redimensionne
		config.disableAudio = false;
		config.width = 1194; //largeur de la fenêtre
		config.height = 574; //hauteur de la fenêtre
		//config.width = 300; //largeur de la fenêtre
		//config.height =600; //hauteur de la fenêtre
		    

	    // fullscreen
	    //config.fullscreen = true;
	    // vSync
	    config.vSyncEnabled = true;
		new LwjglApplication(new App(config), config);

       }
}
