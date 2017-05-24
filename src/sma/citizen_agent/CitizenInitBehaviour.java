package sma.citizen_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
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
import sma.vote_behaviour.CitizenSuspicionListener;
import sma.vote_behaviour.CitizenSuspicionBehaviour;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class CitizenInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;
	private AID receiver;

	public CitizenInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}

	@Override
	public void action() {
		System.out.println("CitizenInitBehaviour THIS PLAYER "+this.agent.getName());
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();

		
		CitizenSuspicionBehaviour citizenSuspicionBehaviour = new CitizenSuspicionBehaviour(this.agent);
		list_behav.add(citizenSuspicionBehaviour);


		CitizenSuspicionListener citizenSuspicionListener = new CitizenSuspicionListener(this.agent);
		list_behav.add(citizenSuspicionListener);

		CitizenSimpleSuspicionBehaviour citizenSimpleSuspicionBehaviour = new CitizenSimpleSuspicionBehaviour(this.agent);
		list_behav.add(citizenSimpleSuspicionBehaviour);
		//CitizenSimpleSuspicionBehaviour NOt generic car this one is for finding werewolf

		this.agent.addBehaviour(citizenSimpleSuspicionBehaviour);
		this.agent.addBehaviour(citizenSuspicionBehaviour);
		this.agent.addBehaviour(citizenSuspicionListener);
		this.agent.getVotingBehaviours().add(citizenSuspicionBehaviour.getName_behaviour());
		
		
		
		
		//No death behaviour
		//this.agent.getDeathBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		
		//Handle attributes
		map_behaviour.put(Roles.CITIZEN, list_behav);

		//enregirstrement NOT FOR CITIZEN CAUZ EVERY PLAYER IS ALREADY A CITIZEN
		
		//Envoie message fin d'initialisation
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);	

	}

}
