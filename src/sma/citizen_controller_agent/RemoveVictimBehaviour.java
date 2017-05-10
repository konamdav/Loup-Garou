package sma.citizen_controller_agent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * Behaviour pour ajouter une victime de jeu
 * @author Davy
 *
 */
public class RemoveVictimBehaviour extends CyclicBehaviour{
	private CitizenControllerAgent citizenController;
	
	public RemoveVictimBehaviour(CitizenControllerAgent citizenController) {
		super();
		this.citizenController = citizenController;
	}

	@Override
	public void action() {
		/*** reception demande de vote **/
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("REMOVE_VICTIM"));

		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			String victim = message.getContent();
			AID aidVictim = new AID(victim);
			int index = 0; 
			boolean flag = false;
			
			while(index<this.citizenController.getVictims().size()&&!flag)
			{
				if(this.citizenController.getVictims().get(index).getName().equals(victim))
				{
					flag = true;
				}
				else
				{
					index++;
				}
			}
	
			if(flag)
			{
				this.citizenController.getVictims().remove(index);
			}
			
			message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("REMOVE_VICTIM_STATUS");
			message.setSender(this.citizenController.getAID());
			message.addReceiver(aidVictim);
			this.citizenController.send(message);
		}
		else
		{
			block();
		}		
	}

}
