package ui.agent;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;
import sma.launch.GameContainer;
import sma.model.DFServices;
import ui.view.App;

public class uiAgent extends Agent  {

	public String test = "test";
	
	public App app = null;
	
	
	
	@Override
	protected void setup() {
	
	   Object[] args = getArguments();
	   App a = (App)args[0];
	   app = a;
	   a.setAgent(this);
	   System.out.println("ok");
		/*
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Werewolf";
		config.resizable = false; //on ne veut pas que l'utilisateur la redimensionne
		config.disableAudio = false;
		config.width = 300; //largeur de la fenêtre
		config.height =600; //hauteur de la fenêtre   
	    config.vSyncEnabled = true;
		new LwjglApplication(new App(config, this), config);
		*/
		//addBehaviour(new QueryContainers(this));
	}

	class QueryContainers extends Behaviour{
		boolean flag; 
		int n_agent; 
		int step;
		uiAgent agent;
		private final ObjectMapper mapper = new ObjectMapper();

		public QueryContainers(uiAgent a)
		{
			agent = a;
		}

		@Override
		public void action() {
			switch(step){
			case 0: 
					ACLMessage m = new ACLMessage(ACLMessage.QUERY_REF);

	        		m.setConversationId("CONTAINERS");
	        		m.setSender(this.agent.getAID());
	        		m.addReceiver(DFServices.getSystemController(this.agent));
					getAgent().send(m);
					step =1;
			
				
					

			case 1:
				MessageTemplate m11 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage message1 = receive(m11);

				if (message1 != null){
					ObjectMapper mapper = new ObjectMapper();
					try {
						List<GameContainer> containers = mapper.readValue(message1.getContent(),  mapper.getTypeFactory().constructCollectionType(List.class, GameContainer.class));
						if (!containers.isEmpty())
						{
							System.out.println("container ok");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							System.out.println("********************************************************************************************************************");
							//app.setContainers(containers);
						}
					}catch(Exception e) {
					}

					flag = true; 
				}
				else block();
			}

		}

		@Override
		public boolean done() {

			return flag;
		}

	}
	
	
	public void addQuery()
	{
		addBehaviour(new QueryContainers(this));
	}
	

}
