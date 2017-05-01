package sma.player_agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sma.generic_agent.AbstractDeathBehaviour;
import sma.generic_agent.FactoryInitBehaviour;
import sma.generic_agent.NewMainRoleBehaviour;
import sma.model.Roles;
import sma.model.SuspicionScore;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import sma.model.DFServices;


/**
 * Structure générique du player
 * @author Davy
 *
 */
public class PlayerAgent extends Agent implements IVotingAgent{
	
	private ArrayList<String> votingBehaviours; //All behaviour for vote to execute. Treeat them with string to send 
	private ArrayList<String> deathBehaviours;//All behaviour for death to execute

	private SuspicionScore suspicionScore; //grille de suspicion
	
	private int gameid;
	private String statut;
	private String main_role; //role given by game controller
	
	//map containing specific behaviours 
	private HashMap<String,ArrayList<Behaviour>> map_role_behaviours;

	public HashMap<String, ArrayList<Behaviour>> getMap_role_behaviours() {
		return map_role_behaviours;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getMain_role() {
		return main_role;
	}

	public void setMain_role(String main_role) {
		this.main_role = main_role;
	}

	public int getGameid() {
		return gameid;
	}

	public void setGameid(int gameid) {
		this.gameid = gameid;
	}

	public PlayerAgent() {
		super();
	}

	@Override
	protected void setup() {
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		this.votingBehaviours = new ArrayList<String>();
		this.main_role = null;
		
		
		this.suspicionScore = new SuspicionScore(); //new suspicion grid
		
		//TODO CEDRIC
		//envoi msg au game controller de type subscribe (conversation id = INIT_PLAYER)
		// pour le prévenir que l'agent a bien été créer

		
		//this.addBehaviour(new RegisterAgentBehaviour(this));
		
		//TODO Pass behaviour stop to public, or send messages. 
		//
		//Behaviour a = new NewMainRoleBehaviour(this); TODO TO list every behaviour 
		
		this.addBehaviour(new NewMainRoleBehaviour(this));
		
		
		
		
		//TODO DO it in a init 
		//TODO CEDRIC 
		//don't forget to create wake/sleep behaviour like @WakeSleepTestBehaviour
		this.addBehaviour(new FactoryInitBehaviour(this));
		this.addBehaviour(new AbstractVoteBehaviour(this));
		this.addBehaviour(new AbstractDeathBehaviour(this));

		DFServices.registerPlayerAgent(Roles.CITIZEN, this, this.gameid);
		DFServices.setStatusPlayerAgent("SLEEP", this, this.gameid);
		
	}
	
	public SuspicionScore getSuspicionScore() {
		return suspicionScore;
	}

	public ArrayList<String> getVotingBehaviours() {
		return this.votingBehaviours;
	}
	

	public ArrayList<String> getDeathBehaviours() {
		return deathBehaviours;
	}

	public String getPlayerName()
	{
		return this.getAID().getLocalName();
	}
	
	
}
