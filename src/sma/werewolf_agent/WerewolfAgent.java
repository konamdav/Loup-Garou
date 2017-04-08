package sma.werewolf_agent;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sma.lover_behaviour.LoverVoteBehaviour;
import sma.player_agent.AbstractVoteBehaviour;
import sma.player_agent.IVotingAgent;

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
		
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("WEREWOLF");
		sd.setName("WEREWOLF");		
		dfad.addServices(sd);
		
		sd = new ServiceDescription();
		sd.setType("CITIZEN");
		sd.setName("CITIZEN");
		dfad.addServices(sd);
		
		System.out.println("AJOUT "+this.getId());
		
		try {
			DFService.register(this, dfad);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		this.addBehaviour(new AbstractVoteBehaviour(this));
		
		//roles
		this.addBehaviour(new WerewolfVoteBehaviour(this));
		this.addBehaviour(new LoverVoteBehaviour(this));
		
	}



	@Override
	public List<String> getVotingBehaviours() {
		return this.votingBehaviours;
	}
	
}
