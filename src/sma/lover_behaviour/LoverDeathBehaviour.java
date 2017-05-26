package sma.lover_behaviour;



import java.util.List;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;
import sma.player_agent.PlayerAgent;

public class LoverDeathBehaviour extends SimpleBehaviour implements IDeathBehaviour {
	private PlayerAgent playerAgent ;
	private String nameBehaviour;

	public LoverDeathBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.nameBehaviour ="LOVER";
	}

	@Override
	public void action() {
		
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("DEATH_"+this.nameBehaviour+"_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			String [] args = {Roles.LOVER, Status.WAKE};
			List<AID> lovers = DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid());
			
			AID myLover = null;
			
			for(AID aid : lovers)
			{
				if(!aid.getName().equals(playerAgent.getAID().getName()))
				{
					myLover = aid;
				}
			}
				
			if(myLover!=null)
			{
				System.out.println("JE TUE MON LOVER");
				List<AID> agents = DFServices.findGameControllerAgent(Roles.CITIZEN, this.playerAgent	, this.playerAgent.getGameid());
				if(!agents.isEmpty())
				{
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("ADD_VICTIM");
					message.setContent(myLover.getName());
					message.setSender(this.playerAgent.getAID());
					message.addReceiver(agents.get(0));
					this.playerAgent.send(message);
				}			
			}
			
			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("DEATH_"+this.nameBehaviour+"_REQUEST");
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
