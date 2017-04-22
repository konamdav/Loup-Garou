package sma.game_controller_agent;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import sma.model.DFServices;
import sma.model.GameSettings;

public class InitBehaviour extends Behaviour {
	private GameControllerAgent gameControllerAgent;	
	private boolean flag;
	private String step;
	private String nextStep;

	private final static String STATE_INIT ="STATE_INIT";
	private final static String STATE_ATTR ="STATE_ATTR";
	private final static String STATE_START_GAME ="STATE_START_GAME";

	public InitBehaviour(GameControllerAgent gameControllerAgent) {
		super();
		this.gameControllerAgent = gameControllerAgent;
		this.flag = false;


		this.step = STATE_INIT;
		this.nextStep = "";

	}

	@Override
	public void action() {
		if(step.equals(STATE_INIT))
		{
			try{
				int nb = this.gameControllerAgent.getGameSettings().getPlayersCount();
				int gameid = this.gameControllerAgent.getGameid();

				Object[] args = {gameid};
				for(int i = 0; i<nb; ++i)
				{
					AgentController ac = this.gameControllerAgent.getContainerController().createNewAgent(
							"PLAYER_"+gameid+""+i, "sma.player_agent.PlayerAgent", args);
					ac.start();
					
					System.out.println("CREATION AGENT PLAYER");
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			this.nextStep = STATE_ATTR;
		}
		else if(step.equals(STATE_ATTR))
		{
			
			List<AID> agents = DFServices.findGameAgent("PLAYER", "CITIZEN", this.gameControllerAgent, this.gameControllerAgent.getGameid());
			Collections.shuffle(agents);
			
			GameSettings gameSettings = this.gameControllerAgent.getGameSettings();
			if(agents.size()!= gameSettings.getPlayersCount())
			{
				System.out.println("GAMEID "+this.gameControllerAgent.getGameid()+" | "+agents.size()+" == "+gameSettings.getPlayersCount()+" ?");
				this.nextStep = STATE_ATTR;
			}
			else
			{
				int indexPlayer = 0;
				for(Entry<String, Integer> entry : gameSettings.getRolesSettings().entrySet())
				{
					if(entry.getValue()>0)
					{
						for(int i = 0; i<entry.getValue(); ++i)
						{
							/** msg attribution role **/
							ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
							messageRequest.setSender(this.gameControllerAgent.getAID());
							messageRequest.addReceiver(agents.get(indexPlayer));
							
							messageRequest.setConversationId("ATTRIBUTION_ROLE");
							
							this.gameControllerAgent.send(messageRequest);
							
							System.out.println("ATTRIBUTION ROLE "+entry.getKey()+" => "+agents.get(indexPlayer).getLocalName());
							indexPlayer++;
						}
					}
				}
				
				this.nextStep = STATE_START_GAME;
			}
			
		}
		else if(step.equals(STATE_START_GAME))
		{
			System.out.println("START GAME");
			//this.gameControllerAgent.addBehaviour(new TurnsBehaviour(this.gameControllerAgent));
			//this.gameControllerAgent.addBehaviour(new CheckEndGameBehaviour(this.gameControllerAgent));
			
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
