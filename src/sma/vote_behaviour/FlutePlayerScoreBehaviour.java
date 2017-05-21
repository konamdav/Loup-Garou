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
public class FlutePlayerScoreBehaviour extends Behaviour{
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

	public FlutePlayerScoreBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "FLUTE_PLAYER_SCORE";

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

			String [] args = {Roles.FLUTE_PLAYER, Status.WAKE};
			String [] args2 = {Roles.CHARMED, Status.WAKE};
			List<AID> fluteplayers = DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid());
			args[1] = Status.SLEEP;
			fluteplayers.addAll(DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid()));
			
			List<AID> charmed = DFServices.findGamePlayerAgent(args2, this.playerAgent, this.playerAgent.getGameid());
			args2[1] = Status.SLEEP;
			charmed.addAll(DFServices.findGamePlayerAgent(args2, this.playerAgent, this.playerAgent.getGameid()));
			
			for(AID player : this.request.getAIDChoices())
			{
				scores.put(player.getName(), this.score(player, request, fluteplayers, charmed));
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


	private int score(AID player,  VoteRequest request, List<AID> fluteplayers, List<AID> charmed)
	{
		VoteResults globalResults = request.getGlobalCitizenVoteResults();
		VoteResults localResults = request.getLocalVoteResults();

		int score = 0;
		if(request.isVoteAgainst()){
			if(player.getName().equals(this.playerAgent.getPlayerName()))
			{
				score = ScoreFactor.SCORE_MIN;
			}
			else
			{
				boolean isFlutePlayer = false;
				for(AID aid : fluteplayers)
				{
					if(player.getName().equals(aid.getName()))
					{
						isFlutePlayer = true;
						score -=100;
					}
					
				}
				
				boolean isCharmed = false;
				for(AID aid : charmed)
				{
					if(player.getName().equals(aid.getName()))
					{
						isCharmed = true;
						score -=100;
					}
					
				}
		
				if(!isFlutePlayer || !isCharmed 
						|| (isFlutePlayer && localResults.getVoteCount(player.getName())!=0)
						|| (isCharmed  && localResults.getVoteCount(player.getName())!=0)
						)
				{			
					// regles de scoring
					score += localResults.getVoteCount(player.getName(), fluteplayers) *ScoreFactor.SCORE_FACTOR_WEREWOLF_VOTE;
					score+= globalResults.getVoteCount(player.getName(), fluteplayers) * ScoreFactor.SCORE_FACTOR_GLOBAL_VOTE;
					
					int diff =0;
					for(AID wolf : fluteplayers)
					{
						diff+= localResults.getDifferenceVote(player.getName(), wolf.getName());
					}
					
					for(AID wolf : charmed)
					{
						diff+= localResults.getDifferenceVote(player.getName(), wolf.getName());
					}
					
					diff = diff/(fluteplayers.size()+charmed.size());
					score+= diff * ScoreFactor.SCORE_FACTOR_DIFFERENCE_LOCAL_VOTE;
				}
				else
				{
					score = ScoreFactor.SCORE_MIN;
				}

			}
		}
		else
		{
			// joueur analys� = joueur 
			if(player.getName().equals(this.playerAgent.getPlayerName()))
			{
				score = 0;
			}
			else
			{
				boolean isFlutePlayer = false;
				for(AID aid : fluteplayers)
				{
					if(player.getName().equals(aid.getName()))
					{
						isFlutePlayer = true;
					}
				}
				
				if(isFlutePlayer)
				{
					score += 100;
				}
			
				boolean isCharmed = false;
				for(AID aid : charmed)
				{
					if(player.getName().equals(aid.getName()))
					{
						isCharmed = true;
						score -=100;
					}
					
				}
				
				if(isCharmed)
				{
					score += 100;
				}
				
				// regles de scoring
				score += localResults.getVoteCount(player.getName(), fluteplayers) *ScoreFactor.SCORE_FACTOR_WEREWOLF_VOTE;
			}
		}
		return score;
	}
	

	public String getName_behaviour() {
		return name_behaviour;
	}
}
