package sma.game_controller_agent;

import jade.core.Agent;
import sma.model.GameSettings;

public class GameControllerAgent extends Agent{
	private GameSettings gameSettings;
	private int gameid;
	
	public GameControllerAgent() {
		super();
	
	}

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		this.gameid = (int) args[0];
		this.gameSettings = (GameSettings) args[1];
		
		this.addBehaviour(new InitBehaviour(this));
	
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public int getGameid() {
		return gameid;
	}
	
	
	
	
}
