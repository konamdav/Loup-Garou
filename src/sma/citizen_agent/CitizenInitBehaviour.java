package sma.citizen_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CitizenScoreBehaviour;
import sma.vote_behaviour.CitizenSuspicionListener;
import sma.vote_behaviour.GenericSuspicionBehaviour;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class CitizenInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	public CitizenInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}
	//TODO TODO Add behaviour for WereWolf

	@Override
	public void action() {		
		GenericSuspicionBehaviour genericSuspicionBehaviour = new GenericSuspicionBehaviour(this.agent);
		CitizenScoreBehaviour citizenScoreBehaviour = new CitizenScoreBehaviour(this.agent);
		CitizenSuspicionListener citizenSuspicionListener = new CitizenSuspicionListener(this.agent);
		
		this.agent.addBehaviour(genericSuspicionBehaviour);
		this.agent.addBehaviour(citizenScoreBehaviour);
		this.agent.addBehaviour(citizenSuspicionListener);
		
		
		this.agent.getVotingBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		this.agent.getVotingBehaviours().add(citizenScoreBehaviour.getName_behaviour());


	}

}
