package sma.werewolf_controller_agent;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.werewolf_controller_agent.TurnBehaviour;
import sma.generic.interfaces.IController;
import sma.model.DFServices;

/**
 * Controlleur gestion du tour citizen
 * @author Davy
 *
 */
public class WerewolfControllerAgent extends Agent implements IController {
	private int gameid;
	
	public WerewolfControllerAgent() {
		super();	

	}
	

	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent("WEREWOLF", this, this.gameid);		
		this.addBehaviour(new sma.generic_vote.SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		
	}


	public int getGameid() {
		return gameid;
	}
	

}
