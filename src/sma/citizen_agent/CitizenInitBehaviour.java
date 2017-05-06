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
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.IVotingAgent;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CitizenScoreBehaviour;
import sma.vote_behaviour.CitizenSimpleSuspicionBehaviour;
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
		CitizenSimpleSuspicionBehaviour citizenSimpleSuspicionBehaviour = new CitizenSimpleSuspicionBehaviour(this.agent);
		
		this.agent.addBehaviour(citizenSimpleSuspicionBehaviour);
		this.agent.addBehaviour(genericSuspicionBehaviour);
		this.agent.addBehaviour(citizenScoreBehaviour);
		this.agent.addBehaviour(citizenSuspicionListener);
		
		
		this.agent.getVotingBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		this.agent.getVotingBehaviours().add(citizenScoreBehaviour.getName_behaviour());
		
		
		//TODO CEDRIC Renvoyer a la fin au game controller un msg INFORM de conversation id ATTRIBUTION_ROLE
		// pour prévenir de la fin de l'attribution du role
		ACLMessage messageRequest = new ACLMessage(ACLMessage.INFORM);
		messageRequest.setSender(this.agent.getAID());
		
		List<AID> agents = DFServices.findGameControllerAgent("GAME", this.myAgent, this.agent.getGameid());
		if(!agents.isEmpty())
		{
			messageRequest.addReceiver(agents.get(0));
			messageRequest.setConversationId("ATTRIBUTION_ROLE");
			this.myAgent.send(messageRequest);	
		}
		


	}

}
