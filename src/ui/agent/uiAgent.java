package ui.agent;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;
import sma.launch.GameContainer;
import sma.model.DFServices;
import sma.model.GameInformations;
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
						List<Integer> containers = mapper.readValue(message1.getContent(),  mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
						if (!containers.isEmpty())
						{
							app.setContainers(containers);
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

	class QueryGameInformations extends TickerBehaviour{


		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		uiAgent agent;
		int gameid;

		public QueryGameInformations(uiAgent a, int id, long period) {
			super(a, period);
			agent = a;
			gameid = id;

		}

		@Override
		protected void onTick() {
			ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
			System.out.println("tick");
			m.setConversationId("GAME_INFORMATIONS");
			m.setSender(this.agent.getAID());
			List<AID> aids =DFServices.findGameControllerAgent("ENVIRONMENT", agent, gameid);
			Collections.shuffle(aids);
			if (!aids.isEmpty())
			{
				m.addReceiver(aids.get(0));
				getAgent().send(m);
			}

		}

	}

	
	
	class QueryGameInformationsNoTick extends Behaviour{

		private static final long serialVersionUID = 1L;
		uiAgent agent;
		int gameid;
		int step;

		public QueryGameInformationsNoTick(uiAgent a, int id) {
			super(a);
			agent = a;
			gameid = id;
			step = 0;

		}
		
			
		protected void onTick() {
			

		}
		@Override
		public void action() {
			switch (step){
			case 0:
				
				ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
				System.out.println("tick");
				m.setConversationId("GAME_INFORMATIONS");
				m.setSender(this.agent.getAID());
				List<AID> aids =DFServices.findGameControllerAgent("ENVIRONMENT", agent, gameid);
				Collections.shuffle(aids);
				if (!aids.isEmpty())
				{
					m.addReceiver(aids.get(0));
					getAgent().send(m);
				}
				step = 1; 
				break;
			case 1:
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = receive(mt);

				if (message != null){
					//System.out.println(message.getContent());
					ObjectMapper mapper = new ObjectMapper();
					try {
						GameInformations gameInformations = mapper.readValue(message.getContent(), GameInformations.class);
						app.setGameInformations(gameInformations);
						step = 0;
					}catch(Exception e) {
					}

				}
				else block();
		
			}
		}
		@Override
		public boolean done() {

			return agent.app.gameInformations != null && agent.app.gameInformations.isEndGame()  ;
		}

	}
	
	
	
	

	public void addQuery()
	{
		addBehaviour(new QueryContainers(this));
	}
	
	public void getInformations(int id)
	{
		//addBehaviour(new QueryGameInformations(this, id, 600));
		addBehaviour(new QueryGameInformationsNoTick(this, id));
	}


}
