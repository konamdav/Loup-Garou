package sma.medium_controller;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import sma.generic.interfaces.IController;
import sma.model.DFServices;

/**
 * Controlleur gestion du tour citizen
 * @author Davy
 *
 */
public class MediumControllerAgent extends Agent implements IController {
	private int gameid;
	
	public MediumControllerAgent() {
		super();	

	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent("MEDIUM", this, this.gameid);		
		this.addBehaviour(new sma.generic.behaviour.SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}
