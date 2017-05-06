package sma.player_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;

/**
 * Interface de vote du player
 * Fait le lien entre l'ext�rieur (controlleur) et les behaviours de scoring
 * @author Davy
 */
public class AbstractVoteBehaviour extends SimpleBehaviour{
	private IVotingAgent agent;
	private int nbVoters;
	private ScoreResults results;
	private List<String> lastResults;
	private List<String> finalResults;

	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SEND_REQUEST = "SEND_REQUEST";
	private final static String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final static String STATE_RESULTS = "RESULTS";
	private final static String STATE_SEND_RESULTS = "SEND_RESULTS";

	private VoteRequest request;

	private String step;
	private String nextStep;
	private AID sender;


	public AbstractVoteBehaviour(IVotingAgent agent) {
		super();
		this.agent = agent;
		this.nbVoters = 0;
		this.results = new ScoreResults();
		this.lastResults = null;
		this.finalResults = null;
		this.request = null;
		
		this.step = STATE_INIT;
		this.nextStep ="";

	}

	
	@Override
	public void action() {

		//System.out.println("STATE = "+this.step);

		if(this.step.equals(STATE_INIT))
		{
			this.nbVoters = 0;
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
				block();
			}
		}
		else if(this.step.equals(STATE_SEND_REQUEST))
		{
			System.out.println("[ "+this.agent.getName()+" ] VOTE REQUEST");
			for(String s : this.agent.getVotingBehaviours())
			{
				System.out.println("ROLE VOTE : "+s);

				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.myAgent.getAID());
				messageRequest.addReceiver(this.myAgent.getAID());
				messageRequest.setConversationId("VOTE_TO_"+s+"_REQUEST");

				ObjectMapper mapper = new ObjectMapper();
				String json ="";
				try {		
					json = mapper.writeValueAsString(request);			
				} catch (Exception e) {
					e.printStackTrace();
				}
				messageRequest.setContent(json);
				this.myAgent.send(messageRequest);

			}

			this.nextStep = STATE_RECEIVE_INFORM;
		}
		else if(this.step.equals(STATE_RECEIVE_INFORM))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_INFORM"));

			ACLMessage message = this.myAgent.receive(mt);

			if(message !=null)
			{
				//System.out.println("\n\nDEBUT PLAYER AGENT : "+this.agent.getName());
				System.err.println(this.agent.getName()+" REQUEST => "+this.request.getRequest());
				System.err.println("MESSAGE INFORM "+" : "+message.getContent());
				
				++this.nbVoters;
				
				ObjectMapper mapper = new ObjectMapper();
				ScoreResults res = new ScoreResults();
				try {
					res = mapper.readValue(message.getContent(), ScoreResults.class);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				this.results.add(res);

				System.err.println("("+this.nbVoters+"/"+this.agent.getVotingBehaviours().size()+")");
				if(this.nbVoters >= this.agent.getVotingBehaviours().size())
				{
					System.err.println("END RECEIVE");
					this.nextStep = STATE_RESULTS;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_INFORM;
				}
			}
			else
			{
				block();
			}
		}
		else if(this.step.equals(STATE_RESULTS))
		{
			this.finalResults = this.results.getFinalResults();
			
			/** equality  ? **/
			if(this.finalResults.size() == 1)
			{
				System.err.println("FINAL RESULTS = "+this.finalResults.size());
				this.nextStep = STATE_SEND_RESULTS;
			}
			else
			{
				if(this.lastResults != null && this.lastResults.equals(this.finalResults))
				{
					/** interblocage **/
					ArrayList<String> tmp = new ArrayList<String>();

					/** random choice  **/
					tmp.add(this.finalResults.get((int)Math.random()*this.finalResults.size()));				
					this.finalResults = tmp;

					System.err.println("FINAL RESULTS = "+this.finalResults.size());
					this.nextStep = STATE_SEND_RESULTS;

				}
				else
				{					
					// new vote with finalists
					
					this.lastResults = this.finalResults;
					
					VoteRequest tmp = new VoteRequest(this.finalResults, request.getGlobalCitizenVoteResults());
					tmp.setRequest(request.getRequest());
					tmp.setVoteAgainst(request.isVoteAgainst());
					
					this.request = tmp;
					this.nbVoters = 0;
					this.nextStep = STATE_SEND_REQUEST;
				}
			}
		}
		else if(this.step.equals(STATE_SEND_RESULTS))
		{
			System.err.println("SEND RESULTS");
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
