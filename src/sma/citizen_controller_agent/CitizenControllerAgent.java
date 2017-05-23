package sma.citizen_controller_agent;

import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.generic.interfaces.IController;
import sma.generic_vote.SynchronousVoteBehaviour;
import sma.model.DFServices;

/**
 * Controlleur gestion du tour citizen
 * @author Davy
 *
 */
public class CitizenControllerAgent extends Agent implements IController {
	private int gameid;
	private Stack<AID> victims;
	private boolean flag_victims;
	
	public CitizenControllerAgent() {
		super();	
		
		flag_victims= false;
		victims = new Stack<AID>();
		
	}
	

	public boolean isFlag_victims() {
		return flag_victims;
	}


	public void setFlag_victims(boolean flag_victims) {
		this.flag_victims = flag_victims;
	}


	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerGameControllerAgent("CITIZEN", this, this.gameid);		
		this.addBehaviour(new SynchronousVoteBehaviour(this));
		this.addBehaviour(new TurnBehaviour(this));
		this.addBehaviour(new AddVictimBehaviour(this));
		this.addBehaviour(new RemoveVictimBehaviour(this));
	}


	public int getGameid() {
		return gameid;
	}
	
	
	public Stack<AID> getVictims() {
		return victims;
	}


}
