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
import sma.model.ForceVoteRequest;
import sma.model.Functions;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

/**
 * Interface de vote du player
 * Fait le lien entre l'extérieur (controlleur) et les behaviours de scoring
 * @author Davy
 */
public class AbstractVoteBehaviour extends SimpleBehaviour{
	private PlayerAgent agent;
	private int nbVoters;
	private ScoreResults results;
	private List<String> lastResults;
	private List<String> finalResults;

	private final String STATE_INIT = "INIT";
	private final String STATE_RECEIVE_FORCE_VOTE = "FORCE_RESULT";
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


	public AbstractVoteBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
		this.nbVoters = 0;
		this.results = new ScoreResults();
		this.lastResults = null;
		this.finalResults = null;
		this.request = null;

		this.forceResults = new HashMap<String, String>();

		this.step = STATE_INIT;
		this.nextStep ="";
	}


	@Override
	public void action() {

		//System.err.println("AV STATE = "+this.step+" for "+this.agent.getLocalName());

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
			//System.err.println("AV receive rqst");
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("VOTE_REQUEST"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				//System.err.println("AV receive rqst");
				this.agent.doWait((int) (Math.random()*500));
				Functions.newActionToLog(this.agent.getLocalName()+" réfléchit", this.agent, this.agent.getGameid());

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
		}//TODO CEDRIC DO THIS IN ORDER TO FORCE CITIZEN VOTE
		
		else if(this.step.equals(STATE_RECEIVE_FORCE_VOTE))
		{
			//System.err.println("AV receive force ?");
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("FORCE_VOTE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				//System.err.println("AV receive force rqst");
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
			}
		}
		else if(this.step.equals(STATE_SEND_REQUEST))
		{
			System.out.println("[ "+this.agent.getName()+" ] VOTE REQUEST "+request.getRequest());
			if(this.forceResults.containsKey(request.getRequest()) && request.getChoices().contains(this.forceResults.get(request.getRequest())))
			{
				String voted = this.forceResults.get(request.getRequest());
				//remove vote
				//this.forceResults.remove(request.getRequest());
				System.err.println("Family vote = "+voted);
				ScoreResults scr = new ScoreResults();
				scr.getResults().put(voted, 1);

				this.results.add(scr);
				this.nextStep = STATE_RESULTS;
			}
			else
			{
				for(String s : this.agent.getVotingBehaviours())
				{
					//System.out.println("ROLE VOTE : "+s);

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
				//System.err.println(this.agent.getName()+" REQUEST => "+this.request.getRequest());
				//System.err.println("MESSAGE INFORM "+" : "+message.getContent());

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

				/** pondération  **/
				String sender = res.getSender();
				if(!sender.isEmpty())
				{
					String type = this.agent.getTypeVotingBehaviours().get(sender);
					
					for(int i = 0; i< this.agent.getFactorVotingBehaviours().get(type);++i)
					{
						this.results.add(res);
					}
				}
				else
				{
					this.results.add(res);
				}
				System.err.println("("+this.nbVoters+"/"+this.agent.getVotingBehaviours().size()+")");
				if(this.nbVoters >= this.agent.getVotingBehaviours().size())
				{
					this.nextStep = STATE_RESULTS;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_INFORM;
				}
			}
			else
			{
				block(1000);
			}
		}
		else if(this.step.equals(STATE_RESULTS))
		{
			this.finalResults = this.results.getFinalResults();

			System.err.println("RESULTS size "+this.finalResults.size());
			
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

					System.err.println("FINAL RESULTS WITH INTERBLOCAGE = "+this.finalResults.size());
					this.nextStep = STATE_SEND_RESULTS;

				}
				else if(!this.request.isAskRequest())
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
				else
				{
					this.nextStep = STATE_SEND_RESULTS;
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

			if(!request.isAskRequest()){

				results.put(this.finalResults.get(0), voter);
				String name = this.finalResults.get(0);
				int index = name.indexOf("@");
				name = name.substring(0, index);
				Functions.newActionToLog(this.agent.getLocalName()+" vote "+name, this.agent, this.agent.getGameid());

			}
			else
			{
				//TRAITEMENT DE LA REQUETE ASK
				//LE RESULTAT EST IL SUFFISANT POUR ETRE OK ?

				if(this.finalResults.size() == 1)
				{
					results.put("OK", voter);
					Functions.newActionToLog(this.agent.getLocalName()+" accepte", this.agent, this.agent.getGameid());

				}
				else
				{
					results.put("NOT_OK", voter);
					Functions.newActionToLog(this.agent.getLocalName()+" refuse", this.agent, this.agent.getGameid());

				}
			}
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
