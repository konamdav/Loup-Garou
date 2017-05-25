package sma.generic.behaviour;

import java.util.List;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.player_agent.PlayerAgent;

/** 
 * Vote asynchrone (les players r�pondent sans attendre les autres)
 * @author Davy
 *
 */
public class DeleteRoleBehaviour extends CyclicBehaviour {
	private PlayerAgent agent;
	
	public DeleteRoleBehaviour(PlayerAgent agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
				MessageTemplate.MatchConversationId("DELETE_ROLE"));

		ACLMessage message = this.agent.receive(mt);
		if (message != null) 
		{
			String role = message.getContent();
			System.err.println("["+this.agent.getName()+"] DELETE ROLE "+role);
			DFServices.deregisterPlayerAgent(role, agent, this.agent.getGameid());
			List<Behaviour> behaviours = this.agent.getMap_role_behaviours().get(role);
			for(Behaviour bhv : behaviours)
			{
				//TODO CEDRIC check if implements IDeathBehaviour/IVoteBehaviour
				
				//stop behaviour
				this.agent.removeBehaviour(bhv);
			}
			
			this.agent.getMap_role_behaviours().remove(role);
		}
		else {
			block();
		}
	}	
}
