package sma.model;

import java.util.ArrayList;
import java.util.List;

public class GameInformations {


	public GameInformations() {
		super();
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
	
}