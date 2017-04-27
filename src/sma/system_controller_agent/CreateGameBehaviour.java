package sma.system_controller_agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;

/***
 * Création d'un jeu via une configuration de partie
 * @author Davy
 *
 */
public class CreateGameBehaviour extends CyclicBehaviour {
	private SystemControllerAgent systemControllerAgent;

	public CreateGameBehaviour(SystemControllerAgent systemControllerAgent) {
		super();
		this.systemControllerAgent = systemControllerAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("CREATE_GAME_REQUEST"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			int gameid = this.systemControllerAgent.getContainers().size();
			this.systemControllerAgent.getContainers().add(new GameContainer(gameid));
		}
		else
		{
			block();
		}
	}
}


