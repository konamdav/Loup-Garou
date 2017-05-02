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
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CitizenScoreBehaviour;
import sma.vote_behaviour.GenericSuspicionBehaviour;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class WerewolfInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	public WerewolfInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}
	//TODO TODO Add behaviour for WereWolf

	@Override
	public void action() {
		//System.out.println("WerewolfInitBehaviour THIS PLAYER "+this.agent.getName());
		
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		//TODO CEDRIC vote behaviour
		//add WerewolfScoreBehaviour
		//add GenericSuspicionBehaviour
		//add CitizenScoreBehaviour
		//add WerewolfSuspicionListener
		// in your map + addBehaviour
		
		WerewolfScoreBehaviour werewolfScoreBehaviour = new WerewolfScoreBehaviour(this.agent);
		GenericSuspicionBehaviour genericSuspicionBehaviour = new GenericSuspicionBehaviour(this.agent);
		CitizenScoreBehaviour citizenScoreBehaviour = new CitizenScoreBehaviour(this.agent);
		WerewolfSuspicionListener werewolfSuspicionListener = new WerewolfSuspicionListener(this.agent);
		
		this.agent.addBehaviour(werewolfScoreBehaviour);
		this.agent.addBehaviour(genericSuspicionBehaviour);
		this.agent.addBehaviour(citizenScoreBehaviour);
		this.agent.addBehaviour(werewolfSuspicionListener);
		
		//TODO CEDRIC 
		//routing for abstractVote behaviour
		//don't forget this.agent.getVotingBehaviours().add(<VOTE BEHAVIOUR>.getName_behaviour())
		this.agent.getVotingBehaviours().add(werewolfScoreBehaviour.getName_behaviour());
		this.agent.getVotingBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		this.agent.getVotingBehaviours().add(citizenScoreBehaviour.getName_behaviour());
		
		//enregirstrement
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.WEREWOLF);
		DFServices.registerPlayerAgent(Roles.WEREWOLF, this.myAgent, this.agent.getGameid());
		
		/*ArrayList<Behaviour> list_vote_behaviour = this.agent.getVotingBehaviours();
		ArrayList<Behaviour> list_all_behaviour = new ArrayList<Behaviour>(); 
		Behaviour vote = new WerewolfVoteBehaviour(this.agent);
		list_all_behaviour.add(vote);
		map_behaviour.put(Roles.WEREWOLF, )*/


	}

}
