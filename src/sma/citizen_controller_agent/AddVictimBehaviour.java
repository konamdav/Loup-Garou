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
			String victim = message.getContent();
			AID aidVictim = new AID(victim);
			this.citizenController.getVictims().push(aidVictim);
		}
		else
		{
			block();
		}		
	}

}
