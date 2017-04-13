package sma.generic.behaviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.citizen_controller_agent.CitizenControllerAgent;
import sma.model.DFServices;
import sma.model.VoteRequest;
import sma.model.VoteResults;

public class SynchronousVoteBehaviour extends Behaviour {
	private int nbVoters;
	private VoteResults results;
	private VoteResults globalResults;
	private List<String> lastResults;
	private List<String> finalResults;
	//private List<String> choices;
	
	private AID agentSender;

	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SEND_REQUEST = "SEND_REQUEST";
	
	private final static String SEND_REQUEST_GLOBAL_VOTE_RESULTS = "SEND_REQUEST_GLOBAL_VOTE_RESULTS";
	private final static String RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS = "RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS";
	
	private final static String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final static String STATE_RESULTS = "RESULTS";
	private final static String STATE_SEND_RESULTS = "SEND_RESULTS";
	private String step;
	private String nextStep;
	
	private VoteRequest request;
	private CitizenControllerAgent ctrl;

	public SynchronousVoteBehaviour(CitizenControllerAgent citizenControllerAgent) {
		super(citizenControllerAgent);
		
		this.ctrl = citizenControllerAgent;
		
		this.nbVoters = 0;
		this.results = new VoteResults();
		this.globalResults = null;
		this.lastResults = null;
		this.finalResults = null;
		this.request = null;
		this.agentSender = null;

		this.step = STATE_INIT;
		this.nextStep ="";

	}


/*	public List<AID> getVoters()
	{
		return DFServices.findGameAgent("PLAYER", "CITIZEN", this.ctrl, this.ctrl.getGameid());
	}
*/
	@Override
	public void action() {

		System.out.println("STATE = "+this.step);

		if(this.step.equals(STATE_INIT))
		{
			this.nbVoters = 0;

			this.globalResults = new VoteResults();
			this.agentSender = null;
			this.request = null;
			
			this.results = new VoteResults();
			this.nextStep = STATE_RECEIVE_REQUEST;


		}
		
		else if(this.step.equals(STATE_RECEIVE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("VOTE_REQUEST"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.agentSender = message.getSender();
				
				ObjectMapper mapper = new ObjectMapper();
				try {
					this.request = mapper.readValue(message.getContent(), VoteRequest.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
					
				this.nextStep = SEND_REQUEST_GLOBAL_VOTE_RESULTS;
			}
			else
			{
				block();
			}

		}
		else if(this.step.equals(SEND_REQUEST_GLOBAL_VOTE_RESULTS))
		{
			List<AID> agents = DFServices.findSystemAgent("CONTROLLER", "ENVIRONMENT", this.myAgent);
			if(!agents.isEmpty())
			{
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setSender(this.myAgent.getAID());
				message.addReceiver(agents.get(0));
				message.setConversationId("GLOBAL_VOTE_RESULTS");
				
				this.myAgent.send(message);
			}
			
			this.nextStep = RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS;
		}
		else if(this.step.equals(RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("GLOBAL_VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				
				System.out.println("CTRL Reception de global results");
				ObjectMapper mapper = new ObjectMapper();
				try {
					this.globalResults = mapper.readValue(message.getContent(), VoteResults.class);
					this.request.setGlobalVoteResults(this.globalResults);
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
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.myAgent.getAID());
			messageRequest.setConversationId("VOTE_REQUEST");

			//VoteRequest request = new VoteRequest(choices, this.globalResults);
			ObjectMapper mapper = new ObjectMapper();

			String json ="";

			try {		
				json = mapper.writeValueAsString(this.request);			
			} catch (Exception e) {
				e.printStackTrace();
			}

			messageRequest.setContent(json);
			messageRequest.addReceiver(this.request.getAIDVoters().get(this.nbVoters));	
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_INFORM;
		}
		else if(this.step.equals(STATE_RECEIVE_INFORM))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_INFORM"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message!=null)
			{
				++this.nbVoters;

				ObjectMapper mapper = new ObjectMapper();
				VoteResults res = new VoteResults();
				try {
					res = mapper.readValue(message.getContent(), VoteResults.class);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				this.results.add(res);
				this.globalResults.add(res);

				if(this.nbVoters>= this.request.getAIDVoters().size())
				{
					this.nextStep = STATE_RESULTS;
				}
				else
				{
					this.nextStep = STATE_SEND_REQUEST;
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

					this.nextStep = STATE_SEND_RESULTS;

				}
				else
				{
					// new vote with finalists
					this.request.setChoices(this.finalResults);
					
					this.results = new VoteResults();
					this.lastResults = this.finalResults;
					this.nbVoters = 0;

					this.nextStep = STATE_SEND_REQUEST;

				}
			}
		}
		else if(this.step.equals(STATE_SEND_RESULTS))
		{
			if(this.finalResults.isEmpty())
			{
				System.err.println("ERROR");
			}
			else
			{
				System.out.println("RESULTS => "+this.finalResults.get(0));
			}

			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValueAsString(this.results);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ACLMessage message = new ACLMessage(ACLMessage.AGREE);
			message.setSender(this.myAgent.getAID());
			message.setConversationId("NEW_VOTE_RESULTS");
			
			List<AID> agents = DFServices.findSystemAgent("CONTROLLER", "ENVIRONMENT", this.myAgent);
			if(!agents.isEmpty())
			{
				message.addReceiver(agents.get(0));
				this.myAgent.send(message);
			}
			
			message = new ACLMessage(ACLMessage.INFORM);
			message.setSender(this.myAgent.getAID());
			message.addReceiver(this.agentSender);
			message.setConversationId("VOTE_RESULTS");
			this.myAgent.send(message);
						
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
