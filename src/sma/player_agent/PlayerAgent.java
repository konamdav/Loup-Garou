package sma.player_agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sma.generic_agent.AbstractDeathBehaviour;
import sma.generic_agent.FactoryInitBehaviour;
import sma.generic_agent.NewMainRoleBehaviour;
import sma.model.Roles;
import sma.model.SuspicionScore;
import sma.werewolf_agent.DeathTestBehaviour;
import sma.werewolf_agent.WakeSleepTestBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
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
		this.deathBehaviours = new ArrayList<String>();

		this.main_role = "";


		this.suspicionScore = new SuspicionScore(); //new suspicion grid



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
		this.addBehaviour(new WakeSleepTestBehaviour(this)); // test david
		this.addBehaviour(new DeathTestBehaviour(this)); //test david 

		//enregistrement par défaut
		System.out.println("[ "+this.getName()+" ] REGISTER "+Roles.CITIZEN);
		DFServices.registerPlayerAgent(Roles.CITIZEN, this, this.gameid);
		DFServices.setStatusPlayerAgent("SLEEP", this, this.gameid);

		
		//TODO CEDRIC Met ça dans un Init Behaviour ( a la fin )
		// préviens le game controller que le joueur est prêt
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


}
