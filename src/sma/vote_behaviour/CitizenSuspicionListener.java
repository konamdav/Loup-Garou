package sma.vote_behaviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.data.ScoreFactor;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.SuspicionScore;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

/**
 * Listener de mouvements pour les citizens
 * @author Davy
 *
 */
public class CitizenSuspicionListener extends Behaviour{
	private PlayerAgent playerAgent;
	private String name_behaviour;
	
	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final static String STATE_SEND_SUSPICIONS = "SEND_NEIHBORS";
	
	private String step;
	private String nextStep;
	
	private SuspicionScore suspicionScore;
	private String side;
	
	public CitizenSuspicionListener(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "CITIZEN_SUSPICION";
		
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
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("MOVE_WEREWOLF"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				this.side = message.getContent();
				this.nextStep = STATE_SEND_SUSPICIONS;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_INFORM;
				block();
			}		
		}
		else if(step.equals(STATE_SEND_SUSPICIONS))
		{
			//liste des voisins que l'on soupçonne
			List<AID> neighbors = DFServices.findNeighborsBySide(this.side, this.playerAgent.getAID(), playerAgent, this.playerAgent.getGameid());
			
			//maj grid
			for(AID aid : neighbors)
			{
				this.suspicionScore.addScore(aid.getLocalName(), ScoreFactor.SCORE_FACTOR_SUSPICION_WEREWOLF);
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

	
	private int score(AID player,  VoteRequest request)
	{
		VoteResults globalResults = request.getGlobalVoteResults();
		VoteResults localResults = request.getLocalVoteResults();
		
		int score = 0;
		// joueur analysé = joueur 
		if(player.getLocalName().equals(this.playerAgent.getPlayerName()))
		{
			score = ScoreFactor.SCORE_MIN;
		}
		else
		{
			// regles de scoring
			score+= globalResults.getVoteCount(player.getLocalName(), this.playerAgent.getPlayerName()) * ScoreFactor.SCORE_FACTOR_GLOBAL_VOTE; 
			score+= globalResults.getVoteCount(player.getLocalName(), this.playerAgent.getPlayerName()) * ScoreFactor.SCORE_FACTOR_GLOBAL_VOTE; 
			
			score+= localResults.getVoteCount(player.getLocalName(), this.playerAgent.getPlayerName()) * ScoreFactor.SCORE_FACTOR_LOCAL_VOTE; 
			score+= localResults.getVoteCount(player.getLocalName(), this.playerAgent.getPlayerName()) * ScoreFactor.SCORE_FACTOR_LOCAL_VOTE; 
		
		}
		
		return score;
		
		
	}
	

	public String getName_behaviour() {
		return name_behaviour;
	}
}
