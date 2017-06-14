package sma.system_controller_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.launch.GameContainer;
import sma.model.DFServices;
import sma.model.GameSettings;

/***
 * Agent systéme
 * Création des conteneurs de jeu
 * @author Davy
 *
 */
public class SystemControllerAgent extends Agent{
	private List<GameContainer> containers;
	public static int id = 0;
	
	public SystemControllerAgent() {
		super();
		containers = new ArrayList<GameContainer>();
	}

	@Override
	protected void setup() {
		
		DFServices.registerSystemControllerAgent(this);

		this.addBehaviour(new CreateGameBehaviour(this));
		this.addBehaviour(new GetGamesBehaviour(this));
		this.addBehaviour(new ReturnContainers(this));
		this.addBehaviour(new RemoveGameBehaviour(this));

		//test init 
		//send msg
		//Decommente pour les logs
		
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.setConversationId("CREATE_GAME_REQUEST");
		message.setSender(this.getAID());
		message.addReceiver(this.getAID());
		 
		GameSettings gameSettings = new GameSettings();
		ObjectMapper mapper = new ObjectMapper();
		String json ="";
		try {
			json = mapper.writeValueAsString(gameSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		message.setContent(json);
		//this.send(message); //DECOMMENT THIS ONE FOR NO LOG 

		//this.send(message);
		//this.send(message);

		
	}

	public void setContainers(List<GameContainer> containers) {
		this.containers = containers;
	}

	public List<GameContainer> getContainers() {
		return containers;
	}

	public List<Integer> getIdContainers(){
		List<Integer> l =  new ArrayList<Integer>();
		for (int i = 0; i < containers.size(); i++)
		{
		    l.add(containers.get(i).getGameid());
		}
		return l;
	}


}
