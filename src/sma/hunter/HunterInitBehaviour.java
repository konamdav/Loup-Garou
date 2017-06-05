package sma.hunter;

import java.util.ArrayList;
import java.util.HashMap;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import sma.lover_behaviour.LoverDeathBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.TypeIA;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CitizenSimpleSuspicionBehaviour;
import sma.vote_behaviour.CitizenSuspicionBehaviour;
import sma.vote_behaviour.CitizenSuspicionListener;
import sma.vote_behaviour.LoverScoreBehaviour;

public class HunterInitBehaviour extends OneShotBehaviour {
	private PlayerAgent agent;
	private AID receiver;

	public HunterInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.agent.addBehaviour(new HunterDeathBehaviour(agent));
		this.receiver = receiver;
	}

	@Override
	public void action() {

		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();

		CitizenSuspicionBehaviour citizenSuspicionBehaviour = new CitizenSuspicionBehaviour(this.agent);
		list_behav.add(citizenSuspicionBehaviour);

		CitizenSuspicionListener citizenSuspicionListener = new CitizenSuspicionListener(this.agent);
		list_behav.add(citizenSuspicionListener);

		CitizenSimpleSuspicionBehaviour citizenSimpleSuspicionBehaviour = new CitizenSimpleSuspicionBehaviour(this.agent);
		list_behav.add(citizenSimpleSuspicionBehaviour);
	
		HunterDeathBehaviour deathBehaviour = new HunterDeathBehaviour(agent);
		list_behav.add(deathBehaviour);
		this.agent.addBehaviour(citizenSimpleSuspicionBehaviour);
		this.agent.addBehaviour(citizenSuspicionBehaviour);
		this.agent.addBehaviour(citizenSuspicionListener);
		
		this.agent.getTypeVotingBehaviours().put(citizenSuspicionBehaviour.getName_behaviour(), TypeIA.SUSPICIOUS);		
		this.agent.getVotingBehaviours().add(citizenSuspicionBehaviour.getName_behaviour());
		this.agent.getDeathBehaviours().add(deathBehaviour.getName_behaviour());
		//Handle attributes
		map_behaviour.put(Roles.HUNTER, list_behav);

		//enregirstrement
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.HUNTER);
		DFServices.registerPlayerAgent(Roles.HUNTER, this.myAgent, this.agent.getGameid());

		//Envoie message fin d'initialisation		
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);	
		

	}

}