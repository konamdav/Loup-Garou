package sma.flute_player;

import java.util.ArrayList;
import java.util.HashMap;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import sma.model.DFServices;
import sma.model.Roles;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.CharmedScoreBehaviour;

public class CharmedInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;
	private AID receiver;

	public CharmedInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}


	@Override
	public void action() {
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
		
		CharmedScoreBehaviour charmedScoreBehaviour = new CharmedScoreBehaviour(this.agent);
		list_behav.add(charmedScoreBehaviour);
		this.agent.addBehaviour(charmedScoreBehaviour);
		this.agent.getVotingBehaviours().add(charmedScoreBehaviour.getName_behaviour());
		map_behaviour.put(Roles.CHARMED, list_behav);
		
		//enregirstrement
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.CHARMED);
		DFServices.registerPlayerAgent(Roles.CHARMED, this.myAgent, this.agent.getGameid());
		
		//Envoie message fin d'initialisation		
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);
	}

}
