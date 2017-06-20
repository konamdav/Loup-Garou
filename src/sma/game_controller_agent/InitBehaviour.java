package sma.game_controller_agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import sma.environment_agent.EnvironmentAgent;
import sma.environmenthuman_agent.EnvironmentHumanAgent;
import sma.model.DFServices;
import sma.model.GameSettings;

//Behaviour √† machine √† √©tat 
//Cr√©e les agents 
//Puis attribue les roles 
//Enfin start le game
/***
 * Creation des players 
 * puis attribution des roles
 * lancement du jeu
 * @author Davy
 *
 */
public class InitBehaviour extends Behaviour {
	private GameControllerAgent gameControllerAgent;	
	private boolean flag;
	private String step;
	private String nextStep;
	private int cpt;

	private final String STATE_INIT ="STATE_INIT";
	private final String STATE_RECEIVE_INIT ="STATE_RECEIVE_INIT";

	private final String STATE_SEND_HUMAN_ATTR ="STATE_HUMAN_ATTR";
	private final String STATE_RECEIVE_HUMAN_ATTR ="STATE_RECEIVE_HUMAN_ATTR";

	private final String STATE_SEND_ATTR ="STATE_ATTR";
	private final String STATE_RECEIVE_ATTR ="STATE_RECEIVE_ATTR";
	private final String STATE_START_GAME ="STATE_START_GAME";
	private HashMap<String, Integer> names;

	public InitBehaviour(GameControllerAgent gameControllerAgent) {
		super();
		this.gameControllerAgent = gameControllerAgent;
		this.flag = false;
		this.cpt = 0;
		
		
		this.names = new HashMap<String, Integer>();
		this.names.put("David", 0);
		this.names.put("Borey", 0);
		this.names.put("Bobo", 0);
		this.names.put("Kyrion", 0);
		this.names.put("Nexus", 0);
		this.names.put("Damien", 0);
		this.names.put("Master", 0);
		this.names.put("CÈdric", 0);
		this.names.put("ClÈment", 0);
		this.names.put("William", 0);
		this.names.put("KÈvin", 0);
		this.names.put("Alexis", 0);
		this.names.put("Benjamin", 0);
		this.names.put("Davy", 0);
		this.names.put("Sienna", 0);
		this.names.put("Yorg", 0);
		this.names.put("Socrate", 0);
		this.names.put("Goubin", 0);
		this.names.put("Romain", 0);
		this.names.put("Mathilde", 0);
		this.names.put("Carole", 0);
		this.names.put("LÈa", 0);
		this.names.put("Elise", 0);
		
		this.names.put("Penelope", 0);
		this.names.put("FranÁois", 0);
		this.names.put("Marine", 0);
		this.names.put("Meluche", 0);
		this.names.put("Lassalle", 0);
		this.names.put("Asselineau", 0);
		this.names.put("Omar", 0);
		
		this.step = STATE_INIT;
		this.nextStep = "";
	
	}

	@Override
	public void action() {
		if(step.equals(STATE_INIT))
		{
			if(this.gameControllerAgent.getGameSettings().getPlayersCount() >0){
				try
				{
					int nb = this.gameControllerAgent.getGameSettings().getPlayersCount();
					int gameid = this.gameControllerAgent.getGameid();
					this.cpt = 0;


					Object[] args = {gameid};
					
					Object[] keys = this.names.keySet().toArray();
					Random r = new Random();
					for(int i = 0; i<nb; ++i)
					{
						String randomName =  keys[r.nextInt(this.names.size())].toString();
						System.err.println("rn "+randomName);
						this.names.put(randomName, this.names.get(randomName)+1);
						int id = this.names.get(randomName);
						String playerName = randomName+"_"+gameid+""+id;
						AgentController ac = this.gameControllerAgent.getContainerController().createNewAgent(
								playerName, "sma.player_agent.PlayerAgent", args);
						ac.start();

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				this.nextStep = STATE_RECEIVE_INIT;
			}
			else
			{
				this.nextStep = STATE_START_GAME;
			}
		}
		else if(step.equals(STATE_RECEIVE_INIT))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE),
					MessageTemplate.MatchConversationId("INIT_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.cpt++;
				if(cpt == this.gameControllerAgent.getGameSettings().getPlayersCount())
				{
					this.nextStep = STATE_SEND_HUMAN_ATTR;
					this.cpt = 0;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_INIT;
				block(1000);
			}
		}

		else if(step.equals(STATE_SEND_HUMAN_ATTR))
		{
			List<AID> agents = DFServices.findGamePlayerAgent( "CITIZEN", this.gameControllerAgent, this.gameControllerAgent.getGameid());
			Collections.shuffle(agents);

			GameSettings gameSettings = this.gameControllerAgent.getGameSettings();

			if(gameSettings.getNbHumans()> 0)
			{
				for(int i = 0; i<gameSettings.getNbHumans(); ++i)
				{
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setSender(this.gameControllerAgent.getAID());
					message.addReceiver(agents.get(i));
					message.setConversationId("INIT_AS_HUMAN");
					this.getAgent().send(message);

					System.out.println("HUMAN PLAYER "+agents.get(i).getName());
					
					if(gameSettings.isGame_mode())
					{
						/** creation d'un env spÈcial human **/
						
						Object[] objects = new Object[3];
						objects[0] = this.gameControllerAgent.getGameid();
						objects[1] = this.gameControllerAgent.getGameSettings();
						objects[2] =  agents.get(i);
						
						AgentController ac;
						try {
							ac = this.myAgent.getContainerController().createNewAgent(
									"ENVIRONMENT_AGENT_"+agents.get(i).getLocalName(), EnvironmentHumanAgent.class.getName(), objects);
							ac.start();
						} catch (StaleProxyException e) {
							e.printStackTrace();
						}
						
					}

				}

				this.nextStep = STATE_RECEIVE_HUMAN_ATTR;
			}
			else
			{
				this.nextStep = STATE_SEND_ATTR;
			}
		}
		else if(step.equals(STATE_RECEIVE_HUMAN_ATTR))
		{
			System.out.println("WAITING HUMAN INIT");
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("INIT_AS_HUMAN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.cpt++;
				if(cpt == this.gameControllerAgent.getGameSettings().getNbHumans())
				{
					this.nextStep = STATE_SEND_ATTR;
					this.cpt = 0;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_HUMAN_ATTR;
				block(1000);
			}
		}
		else if(step.equals(STATE_SEND_ATTR))
		{
			List<AID> agents = DFServices.findGamePlayerAgent( "CITIZEN", this.gameControllerAgent, this.gameControllerAgent.getGameid());
			Collections.shuffle(agents);

			GameSettings gameSettings = this.gameControllerAgent.getGameSettings();

			int indexPlayer = 0;
			//R√©cup√®re tous les Roles du RolesSettings
			for(Entry<String, Integer> entry : gameSettings.getRolesSettings().entrySet())
			{
				if(entry.getValue()>0)
				{
					//Attribue le nombre de joueurs de ce role
					for(int i = 0; i<entry.getValue(); ++i)
					{
						/** msg attribution role **/
						ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
						messageRequest.setSender(this.gameControllerAgent.getAID());
						messageRequest.addReceiver(agents.get(indexPlayer));

						messageRequest.setConversationId("ATTRIBUTION_ROLE");
						messageRequest.setContent(entry.getKey());

						this.gameControllerAgent.send(messageRequest);
						System.out.println("ATTRIBUTION ROLE "+entry.getKey()+" => "+agents.get(indexPlayer).getName());
						indexPlayer++;
					}
				}
			}

			this.nextStep = STATE_RECEIVE_ATTR;


		}
		else if(step.equals(STATE_RECEIVE_ATTR))
		{
			System.out.println("WAITING ATTRIBUTION");
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("ATTRIBUTION_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.cpt++;
				System.out.println("RECEIVE ATTRIBUTION "+this.cpt+"/"+this.gameControllerAgent.getGameSettings().getPlayersCount());



				if(cpt == this.gameControllerAgent.getGameSettings().getPlayersCount())
				{
					this.nextStep = STATE_START_GAME;
					this.cpt = 0;
				}
			}
			else
			{
				this.nextStep = STATE_RECEIVE_ATTR;
				block(1000);
			}
		}
		else if(step.equals(STATE_START_GAME))
		{
			System.err.println(DFServices.findOrderedCitizen(this.getAgent(), this.gameControllerAgent.getGameid()));
			System.out.println("START GAME");

			//System.out.println("PROFILES");
			//DFServices.getPlayerProfiles(this.gameControllerAgent, this.gameControllerAgent.getGameid());
			this.gameControllerAgent.addBehaviour(new WaitStartBehaviour(this.gameControllerAgent));
			this.gameControllerAgent.addBehaviour(new CheckEndGameBehaviour(this.gameControllerAgent));

			this.flag = true;
		}

		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return flag;
	}

}
