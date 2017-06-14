package sma.player_agent;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.Functions;

public class SleepBehaviour extends SimpleBehaviour{
	private PlayerAgent playerAgent ;

	public SleepBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
	}

	@Override
	public void action() {
		MessageTemplate	mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			this.playerAgent.doWait((int) (Math.random()*1000));
			this.playerAgent.setStatutandRegister("SLEEP");
			//Functions.newActionToLog(this.playerAgent.getLocalName()+" s'endort", this.playerAgent, this.playerAgent.getGameid());

			if(playerAgent.isHuman()){
				Functions.decHumans(playerAgent, playerAgent.getGameid());
			}

			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("SLEEP_PLAYER");
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
