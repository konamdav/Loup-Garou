package sma.lover_behaviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.LoverScoreBehaviour;

public class LoverInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;
	private AID receiver;

	public LoverInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}

	@Override
	public void action() {
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();
	
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.LOVER);
		DFServices.registerPlayerAgent(Roles.LOVER, this.myAgent, this.agent.getGameid());

		LoverScoreBehaviour loverScoreBehaviour = new LoverScoreBehaviour(this.agent); 
		list_behav.add(loverScoreBehaviour);
		
		LoverDeathBehaviour loverDeathBehaviour = new LoverDeathBehaviour(this.agent); 
		list_behav.add(loverDeathBehaviour);
		
		this.agent.addBehaviour(loverScoreBehaviour);
		this.agent.getVotingBehaviours().add(loverScoreBehaviour.getName_behaviour());
		
		this.agent.addBehaviour(loverDeathBehaviour);
		this.agent.getDeathBehaviours().add(loverDeathBehaviour.getName_behaviour());
		
		map_behaviour.put(Roles.LOVER, list_behav);
		
		//Envoie message fin d'initialisation		
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);	
		
	}

}
