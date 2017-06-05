package sma.hunter_behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.AbstractDeathBehaviour;
import sma.generic_death.IDeathBehaviour;
import sma.generic_vote.IVotingAgent;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;
import sma.model.VoteRequest;
import sma.player_agent.PlayerAgent;

public class HunterDeathBehaviour extends SimpleBehaviour implements IDeathBehaviour {

	private String name_behaviour;
	private PlayerAgent agent;

	public HunterDeathBehaviour(PlayerAgent agent) {
		super(agent);
		this.agent = agent;
		this.name_behaviour = "HUNTER";
	}

	public String getName_behaviour() {
		return name_behaviour;
	}

	public void setName_behaviour(String name_behaviour) {
		this.name_behaviour = name_behaviour;
	}

	@Override
	public void action() {

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("DEATH_"+this.getName_behaviour()+"_REQUEST"));
		ACLMessage receive = this.myAgent.receive(mt);
		if (receive != null) {
			
			List<AID> agents = DFServices.findGameControllerAgent(Roles.HUNTER, this.agent,
					this.agent.getGameid());
			
			if (!agents.isEmpty()) {
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("START_TURN");
				message.setSender(this.agent.getAID());
				//change le sender en agents.get(0), pour parler au controller des citizens
				
				message.addReceiver(agents.get(0));
				this.agent.send(message);
				System.out.println("i'm dead");
			}
			mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("END_TURN"));
			receive = null;
			receive = this.myAgent.receive(mt);
			if(receive != null){
				ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
				reply.setConversationId("DEATH_"+this.getName_behaviour()+"_REQUEST");
				reply.setSender(this.myAgent.getAID());
				reply.addReceiver(this.myAgent.getAID());
				this.myAgent.send(reply);
				System.out.println("terminé");
			}
			
			
			/*
			List<AID> target = DFServices.findGamePlayerAgent(Roles.WEREWOLF, this.agent, this.agent.getGameid());

			if (!agents.isEmpty()) {
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("ADD_VICTIM");
				message.setContent(target.get(0).getName());
				message.setSender(this.agent.getAID());
				message.addReceiver(agents.get(0));
				this.agent.send(message);
			}
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("DEATH_"+this.getName_behaviour()+"_REQUEST");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(this.myAgent.getAID());
			this.myAgent.send(reply);*/
			
		} else {
			block();
		}
	}

	@Override
	public boolean done() {

		return false;
	}

}
