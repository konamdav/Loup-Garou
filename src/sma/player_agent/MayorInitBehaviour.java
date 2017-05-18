package sma.player_agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import sma.model.DFServices;
import sma.model.Roles;

public class MayorInitBehaviour extends Behaviour {
	private PlayerAgent agent;
	private AID receiver;

	public MayorInitBehaviour(PlayerAgent agent, AID receiver) {
		super();
		this.agent = agent;
		this.receiver = receiver;
	}

	@Override
	public void action() {
		
		//enregistrement
		DFServices.registerPlayerAgent(Roles.MAYOR, this.myAgent, this.agent.getGameid());
		
		//Envoie message fin d'initialisation		
		ACLMessage messageRequest = new ACLMessage(ACLMessage.AGREE);
		messageRequest.setSender(this.agent.getAID());
		messageRequest.setConversationId("INIT_ROLE");
		messageRequest.addReceiver(this.receiver);
		this.myAgent.send(messageRequest);
		
	}

	@Override
	public boolean done() {
		return false;
	}

}
