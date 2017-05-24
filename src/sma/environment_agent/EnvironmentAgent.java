package sma.environment_agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import sma.model.DFServices;
import sma.model.HumanVoteRequest;
import sma.model.VoteResults;

public class EnvironmentAgent extends Agent{
	private VoteResults globalResults;
	private VoteResults currentResults;
	
	private HumanVoteRequest humanVoteRequest;
	
	private Stack<HumanVoteRequest> stackRequest;
	
	private String dayState;
	private List<String> actionLogs;
	private String turn;
	
	private boolean endGame;
	private int gameid;
	
//	private List<AID> listeners;
	
	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		gameid = (int) args[0];
		
		stackRequest = new Stack<HumanVoteRequest>();
		globalResults = new VoteResults();
		currentResults = new VoteResults();
		turn ="INIT";
		endGame = false;
		dayState = "NIGHT";
		
		humanVoteRequest = null;
		
		actionLogs = new ArrayList<String>();
		//listeners = new ArrayList<AID>();
		
		System.err.println("REGISTER ENV");
		DFServices.registerGameControllerAgent("ENVIRONMENT",this,  this.gameid);
		
		this.addBehaviour(new CycleSendBehaviour(this));
		this.addBehaviour(new CycleReceiveBehaviour(this));
		
	}
	public Stack<HumanVoteRequest> getStackRequest() {
		return stackRequest;
	}
	/*public List<AID> getListeners() {
		return listeners;
	}

	public void setListeners(List<AID> listeners) {
		this.listeners = listeners;
	}*/
	public String getTurn() {
		return turn;
	}
	public void setTurn(String turn) {
		this.turn = turn;
	}
	public boolean isEndGame() {
		return endGame;
	}
	public void setEndGame(boolean endGame) {
		this.endGame = endGame;
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
	public HumanVoteRequest getHumanVoteRequest() {
		return humanVoteRequest;
	}
	public void setHumanVoteRequest(HumanVoteRequest humanVoteRequest) {
		this.humanVoteRequest = humanVoteRequest;
	}
	

}
