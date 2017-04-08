package sma.game_controller_agent;

import jade.core.behaviours.SimpleBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class InitBehaviour extends SimpleBehaviour {
	private GameControllerAgent gameControllerAgent;
	private boolean flag_done;

	public InitBehaviour(GameControllerAgent a) {
		super(a);
		this.gameControllerAgent = a;
		this.flag_done = false;
	}

	@Override
	public void action() {
		Object[] objects = new Object[1];
		objects[0] = this.gameControllerAgent.getGameid();

		for(int i = 0; i<this.gameControllerAgent.getGameSettings().getPlayersCount(); ++i)
		{	
			AgentController ac;
			try {
				ac = this.gameControllerAgent.getContainerController().createNewAgent(
						"PLAYER_"+this.gameControllerAgent.getGameid()+i, "sma.player_agent.PlayerAgent", objects);
				ac.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		
		this.flag_done = true;

	}

	@Override
	public boolean done() {
		return this.flag_done;
	}

}
