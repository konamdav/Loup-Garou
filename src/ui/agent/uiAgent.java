package ui.agent;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.view.App;

public class uiAgent extends Agent  {

	public String test = "test";
	
	
	
	@Override
	protected void setup() {
	
	   Object[] args = getArguments();
	   App a = (App)args[0];
	   a.setAgent(this);
	   System.out.println("ok");
		/*
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Werewolf";
		config.resizable = false; //on ne veut pas que l'utilisateur la redimensionne
		config.disableAudio = false;
		config.width = 300; //largeur de la fenêtre
		config.height =600; //hauteur de la fenêtre   
	    config.vSyncEnabled = true;
		new LwjglApplication(new App(config, this), config);
		*/
		addBehaviour(new GetInformation());
	}

	class GetInformation extends CyclicBehaviour
	{

		@Override
		public void action() {
			ACLMessage message;
			MessageTemplate userRequest = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			message = receive(userRequest);

			if (message != null){
				test = "ouii";

			}
			else{
				block();
			}
		}
		
	}
	
	

}
