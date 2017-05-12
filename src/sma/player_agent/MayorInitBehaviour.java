package sma.player_agent;

import jade.core.behaviours.Behaviour;
import sma.model.DFServices;
import sma.model.Roles;

public class MayorInitBehaviour extends Behaviour {
	private PlayerAgent playerAgent;
	
	
	public MayorInitBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
	}

	@Override
	public void action() {
		DFServices.registerPlayerAgent(Roles.MAYOR, this.myAgent, this.playerAgent.getGameid());
	}

	@Override
	public boolean done() {
		return false;
	}

}
