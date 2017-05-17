package sma.werewolf_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_vote.IVotingAgent;
import sma.lover_behaviour.LoverDeathBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CitizenScoreBehaviour;
import sma.vote_behaviour.CitizenSimpleSuspicionBehaviour;
import sma.vote_behaviour.GenericSuspicionBehaviour;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSimpleSuspicionBehaviour;
import sma.vote_behaviour.WerewolfSuspicionBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class WerewolfInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;
	private AID receiver;

	public WerewolfInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}

	@Override
	public void action() {
		System.out.println("WerewolfInitBehaviour THIS PLAYER "+this.agent.getName());
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		//TODO CEDRIC vote behaviour
		// in your map + addBehaviour
		
		WerewolfScoreBehaviour werewolfScoreBehaviour = new WerewolfScoreBehaviour(this.agent);
		list_behav.add(werewolfScoreBehaviour);
		WerewolfSuspicionBehaviour werewolfSuspicionBehaviour = new WerewolfSuspicionBehaviour(this.agent);
		list_behav.add(werewolfSuspicionBehaviour);

		//Not for werewolf, Do in a common one TODO Look if has to keep it 
		WerewolfSuspicionListener werewolfSuspicionListener = new WerewolfSuspicionListener(this.agent);
		list_behav.add(werewolfSuspicionListener);
		//werewolfSuspicionListener //LISTENER AIT MESSAGE for suscipions

		WerewolfSimpleSuspicionBehaviour werewolfSimpleSuspicionBehaviour = new WerewolfSimpleSuspicionBehaviour(this.agent);
		this.agent.addBehaviour(werewolfSimpleSuspicionBehaviour);

		//ScoreBehvaiour is for Vote
		this.agent.addBehaviour(werewolfScoreBehaviour);
		list_behav.add(werewolfScoreBehaviour);

		this.agent.addBehaviour(werewolfSuspicionBehaviour);
		list_behav.add(werewolfSuspicionBehaviour);

		this.agent.addBehaviour(werewolfSuspicionListener);
		list_behav.add(werewolfSuspicionListener);
		
		//TODO CEDRIC 
		//routing for abstractVote behaviour
		//don't forget this.agent.getVotingBehaviours().add(<VOTE BEHAVIOUR>.getName_behaviour())
		this.agent.getVotingBehaviours().add(werewolfScoreBehaviour.getName_behaviour());
		this.agent.getVotingBehaviours().add(werewolfSuspicionBehaviour.getName_behaviour());

		//citizenScoreBehaviour // VOTE CONTRE CEUX DEJA VOTER //TODO DAVID MODIFIER NON TO GENERIC

		//Handle attributes
		map_behaviour.put(Roles.WEREWOLF, list_behav);
		
		//enregirstrement
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.WEREWOLF);
		DFServices.registerPlayerAgent(Roles.WEREWOLF, this.myAgent, this.agent.getGameid());
		
		
		//Envoie message fin d'initialisation
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);	
		

	}

}
