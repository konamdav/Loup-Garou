package sma.environment_agent;

import jade.core.Agent;
import sma.model.DFServices;
import sma.model.VoteResults;

public class EnvironmentAgent extends Agent{
	private VoteResults globalResults;
	private VoteResults currentResults;
	
	@Override
	protected void setup() {
		globalResults = new VoteResults();
		currentResults = new VoteResults();
		
		DFServices.registerSystemAgent("CONTROLLER", "ENVIRONMENT", this);
		this.addBehaviour(new CycleSendBehaviour(this));
		this.addBehaviour(new CycleReceiveBehaviour(this));
		
	}

	public void setGlobalResults(VoteResults globalResults) {
		this.globalResults = globalResults;
	}

	public void setCurrentResults(VoteResults currentResults) {
		this.currentResults = currentResults;
	}

	public VoteResults getGlobalResults() {
		return globalResults;
	}

	public VoteResults getCurrentResults() {
		return currentResults;
	}
	

}
