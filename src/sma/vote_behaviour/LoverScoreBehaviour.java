package sma.vote_behaviour;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.data.ScoreFactor;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.Status;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

/***
 * Algo de scoring pour tous les joueurs 
 * @author Davy
 *
 */
public class LoverScoreBehaviour extends Behaviour{
	private PlayerAgent playerAgent;
	private String name_behaviour;

	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SCORE = "SCORE";
	private final static String STATE_SEND_SCORE = "SEND_SCORE";

	private String step;
	private String nextStep;

	private VoteRequest request;
	private ScoreResults scoreResults;

	public LoverScoreBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "LOVER_SCORE";

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

			String [] args = {Roles.LOVER, Status.WAKE};
			List<AID> agents = DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid());
			
			String[] args2 = {Roles.LOVER, Status.SLEEP};
			agents.addAll(DFServices.findGamePlayerAgent(args2, this.playerAgent, this.playerAgent.getGameid()));
			AID lover = null;
			for(AID aid : agents)
			{
				if(!aid.getName().equals(this.playerAgent.getPlayerName()))
				{
					lover = aid;
				}
			}
			
			
			for(AID player : this.request.getAIDChoices())
			{
				scores.put(player.getName(), this.score(player, request, lover));
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


	private int score(AID player,  VoteRequest request, AID lover)
	{
		VoteResults globalResults = request.getGlobalCitizenVoteResults();
		VoteResults localResults = request.getLocalVoteResults();

		int score = 0;
		if(request.isVoteAgainst()){
			// joueur analysé = joueur 
			if(player.getName().equals(this.playerAgent.getPlayerName()))
			{
				score = ScoreFactor.SCORE_MIN;
			}
			else
			{
				//lover
				if(player.getName().equals(lover.getName()))
				{
					score = ScoreFactor.SCORE_MIN;
				}
				else
				{
					// regles de scoring
					score+= globalResults.getVoteCount(player.getName(), lover.getName()) * ScoreFactor.SCORE_FACTOR_GLOBAL_VOTE; 
					score+= localResults.getVoteCount(player.getName(), lover.getName()) * ScoreFactor.SCORE_FACTOR_LOCAL_VOTE; 
					score+= localResults.getVoteCount(player.getName()) * ScoreFactor.SCORE_FACTOR_LOCAL_NB_VOTE; 
					score+= localResults.getDifferenceVote(player.getName(),lover.getName()) * ScoreFactor.SCORE_FACTOR_DIFFERENCE_LOCAL_VOTE; 
				}
				

			}
		}
		else
		{
			
			// joueur analysé = joueur 
			if(player.getName().equals(this.playerAgent.getPlayerName()))
			{
				score = 0;
			}
			
			else
			{
				//lover ?
				if(player.getName().equals(lover.getName()))
				{
					score+=250;
				}
				
				// regles de scoring
				//lover a déjà voté pour lui
				score += localResults.getVoteCount(player.getName(), lover.getName()) *ScoreFactor.SCORE_FACTOR_LOVER_VOTE; 
				//nb de voix qu'il a déjà
				score += localResults.getVoteCount(player.getName()) *ScoreFactor.SCORE_FACTOR_LOCAL_NB_VOTE;

			}
		}
		return score;


	}
	

	public String getName_behaviour() {
		return name_behaviour;
	}
}
