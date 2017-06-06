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
public class AddVictimBehaviour extends CyclicBehaviour{
	private CitizenControllerAgent citizenController;

	public AddVictimBehaviour(CitizenControllerAgent citizenController) {
		super();
		this.citizenController = citizenController;
	}

	@Override
	public void action() {
		/*** reception demande de vote **/
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("ADD_VICTIM"));

		ACLMessage message = this.myAgent.receive(mt);
		if(message != null)
		{
			System.err.println("AKV : Receive add victim "+message.getContent());
			String victim = message.getContent();
			AID aidVictim = new AID(victim);

			boolean flag = false;
			for(AID aid : this.citizenController.getVictims())
			{
				if(aid.getName().equals(victim))
				{
					flag = true;
				}
			}
			if(!flag){
				this.citizenController.getVictims().push(aidVictim);

				message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("ATTR_VICTIM_STATUS");
				message.setSender(this.citizenController.getAID());
				message.addReceiver(aidVictim);
				this.citizenController.send(message);

			}
		}
		else
		{
			block(1000);
		}		
	}

}
