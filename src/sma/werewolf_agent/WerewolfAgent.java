package sma.werewolf_agent;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sma.generic_vote.AbstractVoteBehaviour;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;

/***
 * 
 * 
 * TEST 
 * @author Davy
 *
 */
public class WerewolfAgent extends Agent implements IVotingAgent{
	private List<String> votingBehaviours;
	private static int cptWerewolf = 0;
	private int id;
	
	private int gameid;
	public WerewolfAgent() {
		super();
		this.votingBehaviours = new ArrayList<String>();
		this.votingBehaviours.add("WEREWOLF");
		this.votingBehaviours.add("LOVER");
		
		this.id = WerewolfAgent.cptWerewolf++;
	
		
	}
	
	
	public int getId() {
		return id;
	}


	@Override
	protected void setup() {
		
		Object[] args = this.getArguments();
		this.gameid = (Integer) args[0];
		
		DFServices.registerPlayerAgent("CITIZEN", this, this.gameid);

		this.addBehaviour(new AbstractVoteBehaviour(this));
		
		//roles
		//this.addBehaviour(new WerewolfVoteBehaviour(this));
		//this.addBehaviour(new LoverVoteBehaviour(this));

		
	}



	public int getGameid() {
		return gameid;
	}


	//@Override
	public List<String> getVotingBehaviours() {
		return this.votingBehaviours;
	}
	
}
