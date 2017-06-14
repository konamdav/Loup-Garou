package sma.generic_vote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ForceVoteRequest;
import sma.model.Functions;
import sma.model.HumanVoteRequest;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;

/**
 * Interface de vote du player
 * Fait le lien entre l'extérieur (controlleur) et les behaviours de scoring
 * @author Davy
 */
public class HumanVoteBehaviour extends SimpleBehaviour{
	private IVotingAgent agent;
	
	private ScoreResults results;
	private List<String> finalResults;

	private final String STATE_INIT = "INIT";
	private final String STATE_RECEIVE_FORCE_VOTE = "FORCE_RESULT";
	
	private final String STATE_SEND_REQUEST_TO_ENV = "SEND_REQUEST_TO_ENV";
	private final String STATE_RECEIVE_HUMAN_VOTE = "RECEIVE_HUMAN_VOTE";
	
	private final String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final String STATE_SEND_REQUEST = "SEND_REQUEST";
	private final String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final String STATE_RESULTS = "RESULTS";
	private final String STATE_SEND_RESULTS = "SEND_RESULTS";

	private VoteRequest request;

	private String step;
	private String nextStep;
	private AID sender;
	private Map<String, String> forceResults;


	public HumanVoteBehaviour(IVotingAgent agent) {
		super();
		this.agent = agent;
	
		this.results = new ScoreResults();
		this.finalResults = null;
		this.request = null;

		this.forceResults = new HashMap<String, String>();

		this.step = STATE_INIT;
		this.nextStep ="";

	}


	@Override
	public void action() 
	{
		if(this.step.equals(STATE_INIT))
		{
			this.request = null;
			this.sender = null;
			this.results = new ScoreResults();
			this.nextStep = STATE_RECEIVE_REQUEST;

		}
		else if(this.step.equals(STATE_RECEIVE_REQUEST))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("VOTE_REQUEST"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.sender = message.getSender();
				ObjectMapper mapper = new ObjectMapper();
				request = new VoteRequest();
				try {
					request = mapper.readValue(message.getContent(), VoteRequest.class);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}

				this.nextStep = STATE_SEND_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_FORCE_VOTE;
			}
		}
		else if(this.step.equals(STATE_RECEIVE_FORCE_VOTE))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("FORCE_VOTE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				ForceVoteRequest forceVoteRequest = new ForceVoteRequest();
				try {
					forceVoteRequest = mapper.readValue(message.getContent(), ForceVoteRequest.class);
					//System.err.println("FORCE VOTE "+forceVoteRequest.getVoteRequest()+ " "+forceVoteRequest.getVoteResult());
					this.forceResults.put(forceVoteRequest.getVoteRequest(), forceVoteRequest.getVoteResult());
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}

				this.nextStep = STATE_RECEIVE_REQUEST;
			}
			else
			{
				this.nextStep = STATE_RECEIVE_REQUEST;
				block(1000);
			}
		}
		else if(this.step.equals(STATE_SEND_REQUEST))
		{
			//System.out.println("[ "+this.agent.getName()+" ] VOTE REQUEST");
			if(this.forceResults.containsKey(request.getRequest()) && request.getChoices().contains(this.forceResults.get(request.getRequest())))
			{
				String voted = this.forceResults.get(request.getRequest());
				//remove vote
				this.forceResults.remove(request.getRequest());
				
				ScoreResults scr = new ScoreResults();
				scr.getResults().put(voted, 1);
				this.results.add(scr);
				
				this.nextStep = STATE_RESULTS;
			}
			else
			{
				//System.err.println("ASK HUMAN REQUEST VOTE "+this.request.getRequest());
				//System.err.println(this.request.getChoices());
				
				Functions.newActionToLog("Vote humain : "+this.request.getRequest(),this.myAgent, this.agent.getGameid());

				
				ACLMessage message = new ACLMessage(ACLMessage.AGREE);
				message.setSender(this.myAgent.getAID());
				message.setConversationId("HUMAN_VOTE_REQUEST");
				
				List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", this.myAgent, this.agent.getGameid());
				if(!agents.isEmpty())
				{
					ObjectMapper mapper = new ObjectMapper();
					HumanVoteRequest humanVoteRequest = new HumanVoteRequest(this.agent.getName(), this.request);
					try {
						String json = mapper.writeValueAsString(humanVoteRequest);
						message.setContent(json);
					} catch (IOException e) {
						e.printStackTrace();
					}
					for(AID aid : agents)
					{
						message.addReceiver(aid);
					}
					this.myAgent.send(message);
					
					System.err.println("SEND TO ENV");
				}
				else
				{

					System.err.println("ERROR");
					Thread.currentThread().stop();
				}
				
				this.nextStep = STATE_RECEIVE_INFORM;
			}
		}
		else if(this.step.equals(STATE_RECEIVE_INFORM))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_INFORM"));

			ACLMessage message = this.myAgent.receive(mt);

			if(message !=null)
			{
				//System.err.println("RECEIVE HUMAN REQUEST VOTE ===> "+message.getContent());
				Functions.newActionToLog("Réponse reçue",this.myAgent, this.agent.getGameid());

				String player = message.getContent();
				this.results.getResults().put(player, 1);
				
				//delete request in environment
				message = new ACLMessage(ACLMessage.REQUEST);
				message.setSender(this.myAgent.getAID());
				message.setConversationId("HUMAN_VOTE_REQUEST");
				
				List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", this.myAgent, this.agent.getGameid());
				if(!agents.isEmpty())
				{
					message.setContent("null");
					for(AID aid : agents)
					{
						message.addReceiver(aid);
					}
					this.myAgent.send(message);
				}			
				
				
				this.nextStep = STATE_RESULTS;
			}
			else
			{
				block(1000);
			}
		}
		else if(this.step.equals(STATE_RESULTS))
		{
			this.finalResults = this.results.getFinalResults();
			this.nextStep = STATE_SEND_RESULTS;

		
		}
		else if(this.step.equals(STATE_SEND_RESULTS))
		{
			//System.err.println("SEND RESULTS");
			HashMap<String, List<String>> results = new HashMap<String, List<String>>();
			VoteResults answer = new VoteResults(results);

			List<String> voter = new ArrayList<String>();
			voter.add(this.agent.getName());
			results.put(this.finalResults.get(0), voter);
			System.err.println("VOTE FOR "+this.finalResults.get(0));

			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("VOTE_INFORM");
			reply.setSender(this.myAgent.getAID());

			reply.addReceiver(sender);

			String json = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(answer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reply.setContent(json);
			this.myAgent.send(reply);

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

}
