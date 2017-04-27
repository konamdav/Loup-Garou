package sma.werewolf_agent;

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

public class WerewolfInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	public WerewolfInitBehaviour(PlayerAgent agent) {
		super();
	}
	//TODO TODO Add behaviour for WereWolf

	@Override
	public void action() {
		System.out.println("WerewolfInitBehaviour THIS PLAYER "+this.agent.getName());
		
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		/*ArrayList<Behaviour> list_vote_behaviour = this.agent.getVotingBehaviours();

		ArrayList<Behaviour> list_all_behaviour = new ArrayList<Behaviour>(); 
		
		Behaviour vote = new WerewolfVoteBehaviour(this.agent);
		list_all_behaviour.add(vote);
		
		map_behaviour.put(Roles.WEREWOLF, )*/


	}

}
