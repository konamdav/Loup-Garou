package sma.game_controller_agent;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import sma.model.DFServices;
import sma.model.GameSettings;

//Behaviour à machine à état 
//Crée les agents 
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

	public InitBehaviour(GameControllerAgent gameControllerAgent) {
		super();
		this.gameControllerAgent = gameControllerAgent;
		this.flag = false;
		this.cpt = 0;
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
					for(int i = 0; i<nb; ++i)
					{
						String playerName = "PLAYER_"+gameid+""+i;
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
				block();
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
				block();
			}
		}
		else if(step.equals(STATE_SEND_ATTR))
		{
			List<AID> agents = DFServices.findGamePlayerAgent( "CITIZEN", this.gameControllerAgent, this.gameControllerAgent.getGameid());
			Collections.shuffle(agents);

			GameSettings gameSettings = this.gameControllerAgent.getGameSettings();

			int indexPlayer = 0;
			//Récupère tous les Roles du RolesSettings
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
				block();
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
