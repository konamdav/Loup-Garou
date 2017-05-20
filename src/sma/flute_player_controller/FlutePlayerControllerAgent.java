package sma.flute_player_controller;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.generic.behaviour.SynchronousVoteBehaviour;
import sma.generic.interfaces.IController;
import sma.model.DFServices;
import sma.model.Roles;

/**
 * Controlleur gestion du tour citizen
 * @author Davy
 *
 */
public class FlutePlayerControllerAgent extends Agent implements IController {
	private int gameid;
	
	public FlutePlayerControllerAgent() {
		super();	
	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent(Roles.FLUTE_PLAYER, this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}
