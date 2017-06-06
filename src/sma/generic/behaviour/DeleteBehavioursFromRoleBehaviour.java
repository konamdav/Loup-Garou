package sma.generic.behaviour;

import java.util.List;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.generic_death.IPreDeathBehaviour;
import sma.model.DFServices;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.IVoteBehaviour;


public class DeleteBehavioursFromRoleBehaviour extends CyclicBehaviour {
	private PlayerAgent agent;
	
	public DeleteBehavioursFromRoleBehaviour(PlayerAgent agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
				MessageTemplate.MatchConversationId("DELETE_BEHAVIOUR"));

		ACLMessage message = this.agent.receive(mt);
		if (message != null) 
		{
			String role = message.getContent();
			System.err.println("["+this.agent.getName()+"] DELETE BEHAVIOURS "+role);
			List<Behaviour> behaviours = this.agent.getMap_role_behaviours().get(role);
			for(Behaviour bhv : behaviours)
			{
				//TODO CEDRIC check if implements IDeathBehaviour/IVoteBehaviour
				if (bhv instanceof IDeathBehaviour){
					//TODO CEDROC Find a way for the getBehaviourName to getName_behaviour
					IDeathBehaviour bhv_death = (IDeathBehaviour) bhv;
					System.out.println("Find a behaviour death to delete "+this.agent.getName()+" behaviour  "+bhv_death.getName_behaviour());
					System.out.println("Get pRE Death Behaviour " +this.agent.getPreDeathBehaviours());
					this.agent.getDeathBehaviours().remove(bhv_death.getName_behaviour());
					System.out.println("After remove pRE Death Behaviour " +this.agent.getPreDeathBehaviours());
				}
				if (bhv instanceof IPreDeathBehaviour){
					IPreDeathBehaviour bhv_death = (IPreDeathBehaviour) bhv;
					System.out.println("Find a behaviour death to delete "+this.agent.getName()+" behaviour  "+bhv_death.getName_behaviour());
					System.out.println("Get Death Behaviour " +this.agent.getDeathBehaviours());
					this.agent.getPreDeathBehaviours().remove(bhv_death.getName_behaviour());
					System.out.println("After remove Death Behaviour " +this.agent.getDeathBehaviours());
				}
				if (bhv instanceof IVoteBehaviour){
					//Security, to not delete the vote behaviour which has been had (False, delete it in the init in the map)
				}	
				else {
					this.agent.removeBehaviour(bhv);
				}
			}

			ACLMessage messageRequest = new ACLMessage(ACLMessage.CONFIRM);
			messageRequest.setSender(this.agent.getAID());
			messageRequest.setConversationId("DELETE_BEHAVIOUR"+role);
			messageRequest.addReceiver(message.getSender());
			this.agent.send(messageRequest);
		}
		else {
			block(1000);
		}
	}	
}
