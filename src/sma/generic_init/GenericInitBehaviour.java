package sma.generic_init;

import java.util.ArrayList;
import java.util.HashMap;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import sma.generic.behaviour.DeleteRoleBehaviour;
import sma.generic_death.AbstractDeathBehaviour;
import sma.generic_vote.AbstractVoteBehaviour;
import sma.player_agent.GetRoleBehaviour;
import sma.player_agent.PlayerAgent;
import sma.player_agent.SleepBehaviour;
import sma.player_agent.WakeBehaviour;
import sma.vote_behaviour.CitizenScoreBehaviour;

public class GenericInitBehaviour extends OneShotBehaviour{
	private PlayerAgent agent;

	//TODO Look which behaviour is common to everyone

	public GenericInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
	}

	@Override
	public void action() {
		//TODO Add beahviour which are right generic
		System.out.println("GenericInitBehaviour THIS PLAYER "+this.agent.getName());
		ArrayList<Behaviour> list_behav = new ArrayList<Behaviour>();
		HashMap<String, ArrayList<Behaviour>> map_behaviour = this.agent.getMap_role_behaviours();

		if(!this.agent.isHuman())
		{
			this.agent.addBehaviour(new AbstractVoteBehaviour(this.agent));
		}
		
		CitizenScoreBehaviour citizenScoreBehaviour = new CitizenScoreBehaviour(this.agent);
		list_behav.add(citizenScoreBehaviour);
		this.agent.addBehaviour(citizenScoreBehaviour);
		this.agent.getVotingBehaviours().add(citizenScoreBehaviour.getName_behaviour()); 
		
		this.agent.addBehaviour(new AbstractDeathBehaviour(this.agent));
		
		WakeBehaviour genericWakeBehaviour = new WakeBehaviour(this.agent);
		this.agent.addBehaviour(genericWakeBehaviour);

		SleepBehaviour genericSleepBehaviour = new SleepBehaviour(this.agent);
		this.agent.addBehaviour(genericSleepBehaviour);
		
		System.err.println("...............................GET ROLE...........................");
		GetRoleBehaviour getRoleBehaviour = new GetRoleBehaviour(this.agent);
		this.agent.addBehaviour(getRoleBehaviour);
		
		DeleteRoleBehaviour deleteRoleBehaviour = new DeleteRoleBehaviour(this.agent);
		this.agent.addBehaviour(deleteRoleBehaviour);
		
		//map_behaviour.put(Roles.GENERIC, list_behav);

	}


}
