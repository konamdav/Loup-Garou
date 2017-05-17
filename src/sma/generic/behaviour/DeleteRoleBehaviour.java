package sma.generic.behaviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.citizen_controller_agent.CitizenControllerAgent;
import sma.model.VoteRequest;
import sma.model.VoteResults;
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
	}

	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
				MessageTemplate.MatchConversationId("DELETE_ROLE"));

		ACLMessage message = this.agent.receive(mt);
		if (message != null) 
		{
						
		}
		else {
			block();
		}
	}	
}
