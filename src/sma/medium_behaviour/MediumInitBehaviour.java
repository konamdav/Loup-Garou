package sma.medium_behaviour;

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
import sma.vote_behaviour.MediumSuspicionListener;
import sma.vote_behaviour.WerewolfScoreBehaviour;
import sma.vote_behaviour.WerewolfSuspicionListener;

public class MediumInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	public MediumInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		
		GenericSuspicionBehaviour genericSuspicionBehaviour = new GenericSuspicionBehaviour(this.agent);
		list_behav.add(genericSuspicionBehaviour);


		CitizenSuspicionListener citizenSuspicionListener = new CitizenSuspicionListener(this.agent);
		list_behav.add(citizenSuspicionListener);

		CitizenSimpleSuspicionBehaviour citizenSimpleSuspicionBehaviour = new CitizenSimpleSuspicionBehaviour(this.agent);
		list_behav.add(citizenSimpleSuspicionBehaviour);
		//CitizenSimpleSuspicionBehaviour NOt generic car this one is for finding werewolf

		MediumSuspicionListener mediumSuspicionListener = new MediumSuspicionListener(this.agent);
		list_behav.add(mediumSuspicionListener);
		
		
		this.agent.addBehaviour(mediumSuspicionListener);
		this.agent.addBehaviour(citizenSimpleSuspicionBehaviour);
		this.agent.addBehaviour(genericSuspicionBehaviour);
		this.agent.addBehaviour(citizenSuspicionListener);
		this.agent.getVotingBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		
		
		
		
		//No death behaviour
		//this.agent.getDeathBehaviours().add(genericSuspicionBehaviour.getName_behaviour());
		
		//Handle attributes
		map_behaviour.put(Roles.MEDIUM, list_behav);
		
		
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.MEDIUM);
		DFServices.registerPlayerAgent(Roles.MEDIUM, this.myAgent, this.agent.getGameid());
		
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
