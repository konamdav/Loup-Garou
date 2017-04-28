package sma.environment_agent;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import sma.model.DFServices;
import sma.model.VoteResults;

public class EnvironmentAgent extends Agent{
	private VoteResults globalResults;
	private VoteResults currentResults;
	
	private String dayState;
	private List<String> actionLogs;
	private String turn;
	
	private boolean endGame;
	private List<AID> listeners;
	
	@Override
	protected void setup() {
		globalResults = new VoteResults();
		currentResults = new VoteResults();
		turn ="INIT";
		dayState = "NIGHT";
		
		actionLogs = new ArrayList<String>();
		listeners = new ArrayList<AID>();
		
		DFServices.registerSystemAgent("CONTROLLER", "ENVIRONMENT", this);
		
		this.addBehaviour(new CycleSendBehaviour(this));
		this.addBehaviour(new CycleReceiveBehaviour(this));
		
	}
	public List<AID> getListeners() {
		return listeners;
	}

	public void setListeners(List<AID> listeners) {
		this.listeners = listeners;
	}
	public String getDayState() {
		return dayState;
	}

	public void setDayState(String dayState) {
		this.dayState = dayState;
	}

	public List<String> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(List<String> actionLogs) {
		this.actionLogs = actionLogs;
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
