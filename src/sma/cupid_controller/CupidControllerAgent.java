package sma.cupid_controller;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.generic.interfaces.IController;
import sma.generic_vote.SynchronousVoteBehaviour;
import sma.model.DFServices;
import sma.model.Roles;

/**
 * Controlleur gestion du tour citizen
 * @author Davy
 *
 */
public class CupidControllerAgent extends Agent implements IController {
	private int gameid;
	
	public CupidControllerAgent() {
		super();	
	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent(Roles.CUPID, this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}
