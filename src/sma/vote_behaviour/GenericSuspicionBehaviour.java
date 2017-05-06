package sma.vote_behaviour;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.data.ScoreFactor;
import sma.model.ScoreResults;
import sma.model.SuspicionScore;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

/***
 * Score en fct de la suspicion
 * @author Davy
 *
 */
public class GenericSuspicionBehaviour extends Behaviour{
	private PlayerAgent playerAgent;
	private String name_behaviour;

	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SCORE = "SCORE";
	private final static String STATE_SEND_SCORE = "SEND_SCORE";

	private String step;
	private String nextStep;

	private VoteRequest request;	
	private SuspicionScore suspicionScore;
	private ScoreResults scoreResults;

	public GenericSuspicionBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "GENERIC_SUSPICION_SCORE";
		this.suspicionScore = this.playerAgent.getSuspicionScore();

		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		if(step.equals(STATE_INIT))
		{
			this.request = null;
			this.scoreResults = null;

			this.nextStep = STATE_RECEIVE_REQUEST;
		}
		else if(step.equals(STATE_RECEIVE_REQUEST))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("VOTE_TO_"+this.name_behaviour+"_REQUEST"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				ObjectMapper mapper = new ObjectMapper();
				this.request = new VoteRequest();

				try {
					this.request = mapper.readValue(message.getContent(), VoteRequest.class);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}

				this.nextStep = STATE_SCORE;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_REQUEST;
				block();
			}		
		}
		else if(step.equals(STATE_SCORE))
		{
			HashMap<String, Integer> scores = new HashMap<String, Integer>();
			scoreResults = new ScoreResults(scores);

			for(AID player : this.request.getAIDChoices())
			{
				scores.put(player.getName(), this.score(player, request));
			}

			this.nextStep =  STATE_SEND_SCORE;

		}
		else if(step.equals(STATE_SEND_SCORE))
		{
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("VOTE_INFORM");
			reply.setSender(this.playerAgent.getAID());
			reply.addReceiver(this.playerAgent.getAID());

			String json = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(this.scoreResults);
			} catch (Exception e) {
				e.printStackTrace();
			}

			reply.setContent(json);
			this.playerAgent.send(reply);

			this.nextStep = STATE_INIT;
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
		SuspicionScore collectiveSuspicion = request.getCollectiveSuspicionScore();

		int score = 0;
		if(player.getName().equals(playerAgent.getName()))
		{
			score = 0;
		}
		else
		{
			score = collectiveSuspicion.getScore(player.getName())+this.suspicionScore.getScore(player.getName())*2000;
			if(!request.isVoteAgainst())
			{
				score = score * -1;
			}
		}
		return score;	
	}


	public String getName_behaviour() {
		return name_behaviour;
	}
}
