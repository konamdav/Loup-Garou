package sma.player_agent;

import java.util.ArrayList;
import java.util.List;


import jade.core.Agent;

public class PlayerAgent extends Agent implements IVotingAgent{
	private List<String> votingBehaviours;
	public PlayerAgent() {
		super();
	}

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		this.votingBehaviours = new ArrayList<String>();
		
		this.addBehaviour(new AbstractVoteBehaviour(this));
		
		//roles
		//this.addBehaviour(new WerewolfVoteBehaviour(this));
		//this.addBehaviour(new LoverVoteBehaviour(this));
		
	}
	
	//@Override
	public List<String> getVotingBehaviours() {
		return this.votingBehaviours;
	}


	
}
