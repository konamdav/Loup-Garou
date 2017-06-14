package sma.game_controller_agent;

import jade.core.Agent;
import sma.generic.interfaces.IController;
import sma.model.DFServices;
import sma.model.GameSettings;

/***
 * Gestionnaire de jeu 
 *** Création des players
 *** Gestion des tours
 * @author Davy
 *
 */
public class GameControllerAgent extends Agent implements IController{
	private GameSettings gameSettings;
	private int gameid;
	private boolean checkEndGame;
	private int num_turn;
	
	public GameControllerAgent() {
		super();
		this.checkEndGame = false; /** var indiquant fin de jeu **/
		this.num_turn = 0;
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

	public int getNum_turn() {
		return num_turn;
	}

	public void setNum_turn(int num_turn) {
		this.num_turn = num_turn;
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public int getGameid() {
		return gameid;
	}

	public void incTurn() {
		++this.num_turn;
		
	}
	
	
	
	
}
