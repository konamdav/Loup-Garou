package sma.medium_controller;

import jade.core.Agent;
import sma.generic.interfaces.IController;
import sma.generic_vote.SynchronousVoteBehaviour;
import sma.model.DFServices;
import sma.model.Roles;

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
		
		DFServices.registerGameControllerAgent(Roles.MEDIUM, this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}
