package sma.player_agent;

import java.util.ArrayList;
import java.util.List;


import jade.core.Agent;
import sma.model.DFServices;

public class PlayerAgent extends Agent implements IVotingAgent{
	private List<String> votingBehaviours;
	private int gameid;
	
	public PlayerAgent() {
		super();
	}

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		this.gameid = (int) args[0];
		
		this.votingBehaviours = new ArrayList<String>();
		this.addBehaviour(new AbstractVoteBehaviour(this));
		
		DFServices.registerGameAgent("PLAYER", "CITIZEN", this, this.gameid);
		
		//roles
		//this.addBehaviour(new WerewolfVoteBehaviour(this));
		//this.addBehaviour(new LoverVoteBehaviour(this));
		
	}
	
	//@Override
	public List<String> getVotingBehaviours() {
		return this.votingBehaviours;
	}


	
}
