package sma.flute_player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.Status;
import sma.model.VoteRequest;
import sma.player_agent.PlayerAgent;

public class FlutePlayerDeathBehaviour extends SimpleBehaviour implements IDeathBehaviour {
	private PlayerAgent playerAgent ;
	private String nameBehaviour;

	public FlutePlayerDeathBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.nameBehaviour ="FLUTE_PLAYER";
	}

	@Override
	public void action() {
		
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("DEATH_"+this.nameBehaviour+"_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			System.err.println("death flute ");
			String [] args = {Roles.FLUTE_PLAYER, Status.WAKE};
			List<AID> flutePlayers = DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid());
			if(flutePlayers.size()<2)
			{
				//il s'agit du denier joueur de flute => sa mort provoque le desenchantement des charmés
				String [] args2 = {Roles.CHARMED, Status.WAKE};
				List<AID> charmed = DFServices.findGamePlayerAgent(args2, this.playerAgent, this.playerAgent.getGameid());
				
				message = new ACLMessage(ACLMessage.CANCEL);
				message.setConversationId("DELETE_ROLE");
				message.setContent(Roles.CHARMED);
				for(AID playerCharmed : charmed)
				{
					message.addReceiver(playerCharmed);
				}
				
				this.playerAgent.send(message);
			}
				
			
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("DEATH_CONFIRM");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());
			this.myAgent.send(reply);
		}
		else{
			block();
		}
	}



	@Override
	public boolean done() {
		return false;
	}

	public String getName_behaviour() {
		return this.nameBehaviour;
	}
}
