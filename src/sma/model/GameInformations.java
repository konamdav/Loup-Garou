package sma.model;

import java.util.ArrayList;
import java.util.List;

public class GameInformations {


	public GameInformations() {
		super();
		this.profiles = null;
		this.dayState = "";
		this.turn = "";
		this.actionLogs = new ArrayList<String>();
		this.currentResults = null;
		this.endGame = false;
		this.num_turn = 0;
	}

	private List<PlayerProfile> profiles; 
	
	public List<PlayerProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<PlayerProfile> profiles) {
		this.profiles = profiles;
	}

	public String getDayState() {
		return dayState;
	}

	public void setDayState(String dayState) {
		this.dayState = dayState;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public List<String> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(List<String> actionLogs) {
		this.actionLogs = actionLogs;
	}

	public VoteResults getCurrentResults() {
		return currentResults;
	}

	public void setCurrentResults(VoteResults currentResults) {
		this.currentResults = currentResults;
	}

	public boolean isEndGame() {
		return endGame;
	}

	public void setEndGame(boolean endGame) {
		this.endGame = endGame;
	}

	private String dayState;
	
	private String turn; 
	
	private List<String> actionLogs;
	
	private VoteResults currentResults;
	
	private boolean endGame;
	
	private int num_turn;

	public int getNum_turn() {
		return num_turn;
	}

	public void setNum_turn(int num_turn) {
		this.num_turn = num_turn;
	}
	
}
