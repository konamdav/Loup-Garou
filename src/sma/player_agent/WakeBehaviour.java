package sma.player_agent;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.Functions;

public class WakeBehaviour extends SimpleBehaviour{
	private PlayerAgent playerAgent ;

	public WakeBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("WAKE_PLAYER"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			this.playerAgent.doWait((int) (Math.random()*2000));
			this.playerAgent.setStatutandRegister("WAKE");
			Functions.newActionToLog(this.playerAgent.getLocalName()+" se reveille", this.playerAgent, this.playerAgent.getGameid());
		
			if(playerAgent.isHuman()){
				Functions.incHumans(playerAgent, playerAgent.getGameid());
			}
			
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("WAKE_PLAYER");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(message.getSender());

			this.myAgent.send(reply);
		}
		else{
			block(1000);
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}
