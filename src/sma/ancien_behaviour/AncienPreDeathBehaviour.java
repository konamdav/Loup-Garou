package sma.ancien_behaviour;



import java.util.List;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.generic_death.IPreDeathBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;
import sma.player_agent.PlayerAgent;

public class AncienPreDeathBehaviour extends SimpleBehaviour implements IPreDeathBehaviour {
	private PlayerAgent playerAgent ;
	private String nameBehaviour;
	private int nb_life;

	public AncienPreDeathBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.nameBehaviour ="ANCIENT";
		this.nb_life = 1;
	}

	
	//Ancien predeath
	//When called once, loose his life,
	//Called second time, so create the DeathBehaviour and add it to his behaviour
	//Create the DeathBehaviour instead (Useless) Do nothing
	@Override
	public void action() {
		
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("PRE_DEATH_"+this.nameBehaviour+"_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			//System.err.println("Start  pre death ancien " + this.playerAgent.getName() + " nb_life " + nb_life);

			ACLMessage reply;
			if (nb_life > 0){
				//System.err.println("Ancien pre death " + this.playerAgent.getName() + " nb_life " + nb_life + " not dead anymoe");
				--nb_life;
				//System.out.println("Ancien pre death " + this.playerAgent.getName() + " nb_life " + nb_life + " not dead anymoe");
				reply = new ACLMessage(ACLMessage.CANCEL);
			}
			else
			{
				//System.err.println("Ancien pre death " + this.playerAgent.getName() + " nb_life " + nb_life + " HAVE to DIED");
				reply = new ACLMessage(ACLMessage.CONFIRM);		
			}
			reply.setConversationId("PRE_DEATH_"+this.nameBehaviour+"_REQUEST");
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

	public String getName_behaviour() {
		return this.nameBehaviour;
	}
}
