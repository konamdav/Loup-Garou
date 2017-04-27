package sma.system_controller_agent;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.launch.GameContainer;

/***
 * Agent système
 * Création des conteneurs de jeu
 * @author Davy
 *
 */
public class SystemControllerAgent extends Agent{
	private List<GameContainer> containers;

	public SystemControllerAgent() {
		super();
		containers = new ArrayList<GameContainer>();
	}

	@Override
	protected void setup() {

		this.addBehaviour(new CreateGameBehaviour(this));
		this.addBehaviour(new GetGamesBehaviour(this));

		//test init 
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.setConversationId("CREATE_GAME_REQUEST");
		message.setSender(this.getAID());
		message.addReceiver(this.getAID());
		this.send(message);
	}

	public void setContainers(List<GameContainer> containers) {
		this.containers = containers;
	}

	public List<GameContainer> getContainers() {
		return containers;
	}




}
