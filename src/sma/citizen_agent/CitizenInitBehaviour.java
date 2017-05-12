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
import sma.vote_behaviour.GenericSuspicionBehaviour;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class CitizenInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	public CitizenInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		System.out.println("CitizenInitBehaviour THIS PLAYER "+this.agent.getName());
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		
		LoverDeathBehaviour loverDeathBehaviour = new LoverDeathBehaviour(this.agent); //FOR TEST
		list_behav.add(loverDeathBehaviour);
		
		GenericSuspicionBehaviour genericSuspicionBehaviour = new GenericSuspicionBehaviour(this.agent);
		list_behav.add(genericSuspicionBehaviour);


		CitizenSuspicionListener citizenSuspicionListener = new CitizenSuspicionListener(this.agent);
		list_behav.add(citizenSuspicionListener);

		CitizenSimpleSuspicionBehaviour citizenSimpleSuspicionBehaviour = new CitizenSimpleSuspicionBehaviour(this.agent);
		list_behav.add(citizenSimpleSuspicionBehaviour);
		//CitizenSimpleSuspicionBehaviour NOt generic car this one is for finding werewolf

		this.agent.addBehaviour(citizenSimpleSuspicionBehaviour);
		
		this.agent.addBehaviour(genericSuspicionBehaviour);
		
		this.agent.addBehaviour(citizenSuspicionListener);
		
		
		this.agent.getVotingBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		
		
		//TEST
		this.agent.addBehaviour(loverDeathBehaviour);

		this.agent.getDeathBehaviours().add(loverDeathBehaviour.getName_behaviour());
		
		//No death behaviour
		//this.agent.getDeathBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		
		//Handle attributes
		map_behaviour.put(Roles.CITIZEN, list_behav);
		
		
		//enregirstrement NOT FOR CITIZEN CAUZ EVERY PLAYER IS ALREADY A CITIZEN
		

		//TODO CEDRIC Renvoyer a la fin au game controller un msg INFORM de conversation id ATTRIBUTION_ROLE
		// pour pr�venir de la fin de l'attribution du role
		//Done in the factoryBehaviour
		/*ACLMessage messageRequest = new ACLMessage(ACLMessage.INFORM);
		messageRequest.setSender(this.agent.getAID());
		
		List<AID> agents = DFServices.findGameControllerAgent("GAME", this.myAgent, this.agent.getGameid());
		if(!agents.isEmpty())
		{
			messageRequest.addReceiver(agents.get(0));
			messageRequest.setConversationId("ATTRIBUTION_ROLE");
			this.myAgent.send(messageRequest);	
		}*/
		


	}

}
