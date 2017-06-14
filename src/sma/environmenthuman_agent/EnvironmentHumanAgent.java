package sma.environmenthuman_agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import sma.model.DFServices;
import sma.model.GameSettings;
import sma.model.HumanVoteRequest;
import sma.model.VoteResults;

/**
 * Agent d'environnement d'une partie
 * Communique avec UI
 * @author Davy
 *
 */
public class EnvironmentHumanAgent extends Agent{
	private AID player;
	private VoteResults globalResults;
	private VoteResults currentResults;
	
	private HumanVoteRequest humanVoteRequest;
	
	private Stack<HumanVoteRequest> stackRequest;
	
	private String dayState;
	private List<String> actionLogs;
	private String turn;
	
	private boolean endGame;
	
	private int gameid;
	private int num_turn;

	/** mode jeu **/
	private boolean game_mode;
	private  int cptHuman;

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		gameid = (int) args[0];
		game_mode = ((GameSettings) (args[1])).isGame_mode();
		player =  ((AID) (args[2]));
		cptHuman = 0;
		num_turn = 0;
		
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
		DFServices.registerGameControllerAgent("ENVIRONMENT_"+this.player.getLocalName(),this,  this.gameid);
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL); 
		parallelBehaviour.addSubBehaviour(new CycleSendBehaviour(this));
		parallelBehaviour.addSubBehaviour(new CycleReceiveBehaviour(this));
		this.addBehaviour(parallelBehaviour);
		
	}
	public boolean isGame_mode() {
		return game_mode;
	}
	public void setGame_mode(boolean game_mode) {
		this.game_mode = game_mode;
	}
	public int getNum_turn() {
		return num_turn;
	}
	public void setNum_turn(int num_turn) {
		this.num_turn = num_turn;
	}
	public int getCptHuman() {
		return cptHuman;
	}
	public void setCptHuman(int cptHuman) {
		this.cptHuman = cptHuman;
	}
	public Stack<HumanVoteRequest> getStackRequest() {
		return stackRequest;
	}

	public AID getPlayer() {
		return player;
	}
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
	public int getGameid() {
		return gameid;
	}
	public void setGameid(int gameid) {
		this.gameid = gameid;
	}

}
