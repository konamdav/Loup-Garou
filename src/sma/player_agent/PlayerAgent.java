package sma.player_agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sma.generic_init.FactoryInitBehaviour;
import sma.generic_init.NewMainRoleBehaviour;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.SuspicionScore;
import sma.werewolf_agent.DeathTestBehaviour;
import sma.werewolf_agent.InitAsHumanBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


/**
 * Structure g�n�rique du player
 * @author Davy
 *
 */
public class PlayerAgent extends Agent implements IVotingAgent{
	private ArrayList<String> votingBehaviours; //All behaviour for vote to execute. Treeat them with string to send 
	private ArrayList<String> deathBehaviours;//All behaviour for death to execute

	//Tour du role ce fait par réception message (LG, Cupidon)
	
	
	private SuspicionScore suspicionScore; //grille de suspicion
	private boolean human;
	private int gameid;
	private String statut;
	private String main_role; //role given by game controller

	//map containing specific behaviours for each role
	//TODO when lost role do the conversion entre le tableau de behaviour et le votingBehaviours et deathBehaviours
	private HashMap<String,ArrayList<Behaviour>> map_role_behaviours;

	public HashMap<String, ArrayList<Behaviour>> getMap_role_behaviours() {
		return map_role_behaviours;
	}

	public String getStatut() {
		return statut;
	}

	//When change Statut, Register to DF
	public void setStatut(String statut) {
		this.statut = statut;
	}
	
	public void setStatutandRegister(String statut) {
		this.statut = statut;
		DFServices.setStatusPlayerAgent(statut, this, this.gameid);

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
		this.human = false;
		this.votingBehaviours = new ArrayList<String>();
		this.deathBehaviours = new ArrayList<String>();

		this.main_role = "";


		this.suspicionScore = new SuspicionScore(); //new suspicion grid

		this.map_role_behaviours = new HashMap<String,ArrayList<Behaviour>>();


		//this.addBehaviour(new RegisterAgentBehaviour(this));

		//TODO Pass behaviour stop to public, or send messages. 
		//
		//Behaviour a = new NewMainRoleBehaviour(this); TODO TO list every behaviour 


		this.addBehaviour(new NewMainRoleBehaviour(this));		
		//TODO DO it in a init 
		//TODO CEDRIC 
		//don't forget to create wake/sleep behaviour like @WakeSleepTestBehaviour
		this.addBehaviour(new InitAsHumanBehaviour(this));
		this.addBehaviour(new FactoryInitBehaviour(this));

		//enregistrement par d�faut
		System.out.println("[ "+this.getName()+" ] REGISTER "+Roles.CITIZEN);
		DFServices.registerPlayerAgent(Roles.CITIZEN, this, this.gameid);
		
		
		this.setStatutandRegister("SLEEP");
		
		//TODO CEDRIC Met �a dans un Init Behaviour ( a la fin )
		// pr�viens le game controller que le joueur est pr�t
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setConversationId("INIT_PLAYER");
		msg.setSender(this.getAID());

		List<AID> agents = DFServices.findGameControllerAgent("GAME", this, this.gameid);
		if(!agents.isEmpty())
		{
			msg.addReceiver(agents.get(0));
			this.send(msg);
		}


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
		return this.getAID().getName();
	}

	public boolean isHuman() {
		return human;
	}
	

	public void setHuman(boolean human) {
		this.human = human;
	}
	
}
