package sma.system_controller_agent;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;
import sma.model.GameSettings;

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
			GameSettings gameSettings = new GameSettings();
			ObjectMapper mapper = new ObjectMapper();
			try {
				gameSettings = mapper.readValue(message.getContent(), GameSettings.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int gameid = this.systemControllerAgent.getContainers().size();
			this.systemControllerAgent.getContainers().add(new GameContainer(gameid, gameSettings));
		}
		else
		{
			block();
		}
	}
}


