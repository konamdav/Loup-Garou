package sma.voleur_controller;

import jade.core.Agent;
import sma.generic.interfaces.IController;
import sma.generic_vote.SynchronousVoteBehaviour;
import sma.model.DFServices;
import sma.model.Roles;

/**
 * Controlleur gestion du tour voleur
 * @author Cedric
 *
 */
public class VoleurControllerAgent extends Agent implements IController {
	private int gameid;
	
	public VoleurControllerAgent() {
		super();	
	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent(Roles.VOLEUR, this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this)); 
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}