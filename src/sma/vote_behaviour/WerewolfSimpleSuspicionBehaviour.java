package sma.vote_behaviour;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import sma.model.SuspicionScore;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

/***
 * Renvoi simple suspicion (grille de 0 ou de 1) pour la suspection collective
 * @author Davy
 *
 */
public class WerewolfSimpleSuspicionBehaviour extends Behaviour{
	private PlayerAgent playerAgent;
	private String name_behaviour;
	
	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SIMPLE_SUSPICION = "SIMPLE_SUSPICION";
	private final static String STATE_SEND_SIMPLE_SUSPICION = "SEND_SIMPLE_SUSPICION";
	
	private String step;
	private String nextStep;
	
	private VoteRequest request;	
	private SuspicionScore suspicionScore;
	private SuspicionScore simpleSuspicionScore;
	private AID sender = null;
	
	public WerewolfSimpleSuspicionBehaviour(PlayerAgent playerAgent) {
		super();
		this.playerAgent = playerAgent;
		this.name_behaviour = "WEREWOLF_SIMPLE_SUSPICION";
		this.suspicionScore = this.playerAgent.getSuspicionScore();
		
		this.sender = null;
		
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		if(step.equals(STATE_INIT))
		{
			this.request = null;
			this.simpleSuspicionScore = null;
			this.sender = null;
			
			this.nextStep = STATE_RECEIVE_REQUEST;
		}
		else if(step.equals(STATE_RECEIVE_REQUEST))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("GET_SIMPLE_SUSPICION"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				this.sender = message.getSender();
				ObjectMapper mapper = new ObjectMapper();
				this.request = new VoteRequest();

				try {
					this.request = mapper.readValue(message.getContent(), VoteRequest.class);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				this.nextStep = STATE_SIMPLE_SUSPICION;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_REQUEST;
				block();
			}		
		}
		else if(step.equals(STATE_SIMPLE_SUSPICION))
		{
			simpleSuspicionScore = new SuspicionScore();
			Map<String, Integer> scores = simpleSuspicionScore.getScore();

			String [] args = {Roles.WEREWOLF, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.playerAgent, this.playerAgent.getGameid());
			List<AID> neighbors = DFServices.findNeighbors(this.playerAgent.getAID(), this.playerAgent, this.playerAgent.getGameid());

			
			for(AID player : this.request.getAIDChoices())
			{
				scores.put(player.getName(), this.score(player, request, werewolves, neighbors));
			}
			
			this.nextStep =  STATE_SEND_SIMPLE_SUSPICION;

		}
		else if(step.equals(STATE_SEND_SIMPLE_SUSPICION))
		{
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("GET_SIMPLE_SUSPICION");
			reply.setSender(this.playerAgent.getAID());
			reply.addReceiver(this.sender);

			String json = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(this.simpleSuspicionScore);
				
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

	
	private int score(AID player,  VoteRequest request, List<AID> werewolves, List<AID> neighbors)
	{
		int score = 0;
		if(!request.isCanBeFake()){
			score = this.suspicionScore.getScore(player.getName());
			score = (score > 0) ? 1 : 0;
		}
		else
		{
			boolean isWerewolf = false;
			boolean isNeighbor = false;
			
			for(AID aid : werewolves)
			{
				if(player.getName().equals(aid.getName()))
				{
					isWerewolf = true;
				}
				
			}
			
			for(AID aid : neighbors)
			{
				if(player.getName().equals(aid.getName()))
				{
					isNeighbor = true;
				}
				
			}
			
			score = (!isWerewolf && isNeighbor) ? 1 : 0;
			
		}
		return score;	
	}
	

	public String getName_behaviour() {
		return name_behaviour;
	}
}
