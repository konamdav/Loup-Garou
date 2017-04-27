package sma.game_controller_agent;

import generic.agent.IController;
import jade.core.Agent;
import sma.model.DFServices;
import sma.model.GameSettings;
//il a un gamesetting qui contient la conf de la partie (roles autorisés + nombre par role)
//C'est le game controlleur qui créé les joueurs + donne un role


public class GameControllerAgent extends Agent implements IController{
	private GameSettings gameSettings;
	private int gameid;
	private boolean checkEndGame;
	
	public GameControllerAgent() {
		super();
		this.checkEndGame = false; /** var indiquant fin de jeu **/
	}

	public boolean isCheckEndGame() {
		return checkEndGame;
	}

	public void setCheckEndGame(boolean checkEndGame) {
		this.checkEndGame = checkEndGame;
	}

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		this.gameid = (Integer)args[0];
		this.gameSettings = (GameSettings) args[1];
		
		DFServices.registerGameControllerAgent("GAME", this, gameid);
		
		this.addBehaviour(new InitBehaviour(this));
		
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public int getGameid() {
		return gameid;
	}
	
	
	
	
}
