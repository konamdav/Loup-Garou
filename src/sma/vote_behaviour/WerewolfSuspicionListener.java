package sma.vote_behaviour;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.SuspicionScore;
import sma.player_agent.PlayerAgent;

/**
 * 	Listener de mouvement pour loup garou
 * @author Davy
 *
 */
public class WerewolfSuspicionListener extends Behaviour{
	private PlayerAgent playerAgent;
	private String name_behaviour;

	public String getName_behaviour() {
		return name_behaviour;
	}


	private final String STATE_INIT = "INIT";
	private final String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final String STATE_SUSPICION_LITTLE_GIRL = "SUSPICION_LITTLE_GIRL";


	private String step;
	private String nextStep;

	private SuspicionScore suspicionScore;
	private String side;

	public WerewolfSuspicionListener(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "WEREWOLF_SUSPICION";

		this.suspicionScore = this.playerAgent.getSuspicionScore();
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		if(step.equals(STATE_INIT))
		{

			this.side = "";
			this.nextStep = STATE_RECEIVE_INFORM;
		}
		else if(step.equals(STATE_RECEIVE_INFORM))
		{
			/** alerte mouvement d'un citizen durant la nuit (role important)**/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("MOVE_LITTLE_GIRL"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				this.side = message.getContent();
				this.nextStep = STATE_SUSPICION_LITTLE_GIRL;
			}
			else
			{
				block(1000);
			}		
		}
		else if(step.equals(STATE_SUSPICION_LITTLE_GIRL))
		{
			List<AID> neighbors = DFServices.findNeighborsBySide(this.side, this.playerAgent.getAID(), playerAgent, this.playerAgent.getGameid());

			//envoi a la suspicion citizen
			//maj grid
			for(AID aid : neighbors)
			{
				this.suspicionScore.addScore(aid.getName(), 10);
			}

			this.nextStep =  STATE_INIT;
		}
		

		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return false;
	}

}
