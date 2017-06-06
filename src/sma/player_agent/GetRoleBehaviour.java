package sma.player_agent;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetRoleBehaviour extends SimpleBehaviour{
		private PlayerAgent playerAgent ;

		public GetRoleBehaviour(PlayerAgent playerAgent) {
			super();
			this.playerAgent = playerAgent;
		}

		@Override
		public void action() {
			MessageTemplate	mt = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchConversationId("GET_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				System.err.println("THIS IS MY ROLE "+this.playerAgent.getMain_role());
				
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				reply.setConversationId("GET_ROLE");
				reply.setContent(this.playerAgent.getMain_role());
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