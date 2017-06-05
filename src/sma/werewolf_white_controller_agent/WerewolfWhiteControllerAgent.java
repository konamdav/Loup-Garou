package sma.werewolf_white_controller_agent;

import jade.core.Agent;
import sma.generic.interfaces.IController;
import sma.model.DFServices;
import sma.model.Roles;

/**
 * Controlleur gestion du tour Werwolf White
 * @author Cédric
 *
 */
public class WerewolfWhiteControllerAgent extends Agent implements IController {
	private int gameid;
	
	public WerewolfWhiteControllerAgent() {
		super();	

	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent(Roles.WHITE_WEREWOLF, this, this.gameid);		
		this.addBehaviour(new sma.generic_vote.SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
	}


	public int getGameid() {
		return gameid;
	}
	

}
