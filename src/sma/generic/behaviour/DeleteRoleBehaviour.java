package sma.generic.behaviour;

import java.util.List;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.generic_death.IDeathBehaviour;
import sma.generic_death.IPreDeathBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.player_agent.PlayerAgent;
import sma.vote_behaviour.IVoteBehaviour;

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
			
			//si on supprime loup blanc ou méchant loup, il faut enlever le role werewolf aussi
			if(role.equals(Roles.GREAT_WEREWOLF) || role.equals(Roles.WHITE_WEREWOLF))
			{
				DFServices.deregisterPlayerAgent(Roles.WEREWOLF, agent, this.agent.getGameid());
			}
			
			List<Behaviour> behaviours = this.agent.getMap_role_behaviours().get(role);
			for(Behaviour bhv : behaviours)
			{
				//TODO Cedric test the preDeathBehaviour
				if (bhv instanceof IPreDeathBehaviour){
					IPreDeathBehaviour bhv_death = (IPreDeathBehaviour) bhv;
					//System.out.println("Find a behaviour death to delete "+this.agent.getName()+" behaviour  "+bhv_death.getName_behaviour());
					System.out.println("Get pRE Death Behaviour " +this.agent.getDeathBehaviours());
					this.agent.getPreDeathBehaviours().remove(bhv_death.getName_behaviour());
					System.out.println("After remove pRE Death Behaviour " +this.agent.getDeathBehaviours());
				}
				else if (bhv instanceof IDeathBehaviour){
					IDeathBehaviour bhv_death = (IDeathBehaviour) bhv;
					//System.out.println("Find a behaviour death to delete "+this.agent.getName()+" behaviour  "+bhv_death.getName_behaviour());
					System.out.println("Get Death Behaviour " +this.agent.getDeathBehaviours());
					this.agent.getDeathBehaviours().remove(bhv_death.getName_behaviour());
					System.out.println("After remove Death Behaviour " +this.agent.getDeathBehaviours());
				}
				else if (bhv instanceof IVoteBehaviour){
					IVoteBehaviour bhv_Vote = (IVoteBehaviour) bhv;
					//System.out.println("Find a behaviour vote to delete "+this.agent.getName()+" behaviour  "+bhv_Vote.getName_behaviour());
					System.out.println("Get Vote Behaviour " +this.agent.getVotingBehaviours());
					this.agent.getVotingBehaviours().remove(bhv_Vote.getName_behaviour());
					System.out.println("After remove Vote Behaviour " +this.agent.getVotingBehaviours());
				}
				//stop behaviour
				this.agent.removeBehaviour(bhv);
			}

			if (this.agent.getMain_role().equals(role)){
				//System.err.println("CHANGE MAIN ROLLE " +this.agent.getMain_role());
				this.agent.setMain_role(""); //TODO Cedric test this one
				//System.out.println("After MAIN ROLLE " +this.agent.getMain_role() + " is empty " + this.agent.getMain_role().isEmpty());
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
