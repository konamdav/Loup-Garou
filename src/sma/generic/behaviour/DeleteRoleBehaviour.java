package sma.generic.behaviour;

import java.util.List;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.model.DFServices;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.IVoteBehaviour;

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

	//TODO David Shaman relou, beahviour to keep

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
				if (bhv instanceof IDeathBehaviour){
					//TODO CEDROC Find a way for the getBehaviourName to getName_behaviour
					IDeathBehaviour bhv_death = (IDeathBehaviour) bhv;
					//System.out.println("Find a behaviour death to delete "+this.agent.getName()+" behaviour  "+bhv_death.getName_behaviour());
					//System.out.println("Get Death Behaviour " +this.agent.getDeathBehaviours());
					this.agent.getDeathBehaviours().remove(bhv_death.getName_behaviour());
					//System.out.println("After remove Death Behaviour " +this.agent.getDeathBehaviours());
				}
				else if (bhv instanceof IVoteBehaviour){
					IVoteBehaviour bhv_Vote = (IVoteBehaviour) bhv;
					//System.out.println("Find a behaviour vote to delete "+this.agent.getName()+" behaviour  "+bhv_Vote.getName_behaviour());
					//System.out.println("Get Vote Behaviour " +this.agent.getVotingBehaviours());
					this.agent.getVotingBehaviours().remove(bhv_Vote.getName_behaviour());
					//System.out.println("After remove Vote Behaviour " +this.agent.getVotingBehaviours());
				}
				//stop behaviour
				this.agent.removeBehaviour(bhv);
			}

			this.agent.getMap_role_behaviours().remove(role);

			ACLMessage messageRequest = new ACLMessage(ACLMessage.CONFIRM);
			messageRequest.setSender(this.agent.getAID());
			messageRequest.setConversationId("DELETE_ROLE"+role);
			messageRequest.addReceiver(message.getSender());
			this.agent.send(messageRequest);
		}
		else {
			block();
		}
	}	
}
