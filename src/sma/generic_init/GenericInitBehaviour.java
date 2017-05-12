package sma.generic_init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.citizen_agent.CitizenInitBehaviour;
import sma.generic_death.AbstractDeathBehaviour;
import sma.generic_vote.AbstractVoteBehaviour;
import sma.lover_behaviour.LoverInitBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;
import sma.player_agent.SleepBehaviour;
import sma.player_agent.WakeBehaviour;
import sma.vote_behaviour.CitizenScoreBehaviour;
import sma.werewolf_agent.WerewolfInitBehaviour;

public class GenericInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	//TODO Look which behaviour is common to everyone

	public GenericInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		//TODO Add beahviour which are right generic
		System.out.println("GenericInitBehaviour THIS PLAYER "+this.agent.getName());
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		
		CitizenScoreBehaviour citizenScoreBehaviour = new CitizenScoreBehaviour(this.agent);
		list_behav.add(citizenScoreBehaviour);
		this.agent.addBehaviour(citizenScoreBehaviour);
		this.agent.getVotingBehaviours().add(citizenScoreBehaviour.getName_behaviour()); 
		
		
		this.agent.addBehaviour(new AbstractDeathBehaviour(this.agent));
		
		WakeBehaviour genericWakeBehaviour = new WakeBehaviour(this.agent);
		this.agent.addBehaviour(genericWakeBehaviour);

		SleepBehaviour genericSleepBehaviour = new SleepBehaviour(this.agent);
		this.agent.addBehaviour(genericSleepBehaviour);
		
		//map_behaviour.put(Roles.GENERIC, list_behav);

	}


}
