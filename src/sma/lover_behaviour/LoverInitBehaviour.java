package sma.lover_behaviour;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
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

public class LoverInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;
	private AID receiver;

	public LoverInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}
	
	//TODO

	@Override
	public void action() {

		//enregistrement TODO CHECK IF LOVER MUST CHECK
		System.out.println("[ "+this.agent.getName()+" ] REGISTER "+Roles.LOVER);
		DFServices.registerPlayerAgent(Roles.LOVER, this.myAgent, this.agent.getGameid());

		//Envoie message fin d'initialisation		
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);	
		
	}

}
