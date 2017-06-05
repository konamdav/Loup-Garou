package sma.game_controller_agent;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import sma.model.DFServices;
import sma.model.GameSettings;

//Behaviour à machine à état 
//Crée les agents 
//Puis attribue les roles 
//Enfin start le game
/***
 * Creation des players 
 * puis attribution des roles
 * lancement du jeu
 * @author Davy
 *
 */
public class WaitStartBehaviour extends WakerBehaviour {
	private GameControllerAgent gameControllerAgent;	
	
	public WaitStartBehaviour(GameControllerAgent gameControllerAgent) {
		super(gameControllerAgent, 7000);
		this.gameControllerAgent = gameControllerAgent;

	}

	@Override
	public void onWake() {
		this.gameControllerAgent.addBehaviour(new TurnsBehaviour(this.gameControllerAgent));
	}


}
