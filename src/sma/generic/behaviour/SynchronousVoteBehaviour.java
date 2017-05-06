package sma.generic.behaviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import generic.interfaces.IController;
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
import sma.model.SuspicionScore;
import sma.model.VoteRequest;
import sma.model.VoteResults;


/***
 * Vote synchrone
 * Attente qu'un joueur finisse de voter pour demander au suivant
 * @author Davy
 *
 */
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
	
	private final static String STATE_GET_SIMPLE_SUSPICION = "GET_SIMPLE_SUSPICION";
	private final static String STATE_ADD_SIMPLE_SUSPICION = "ADD_SIMPLE_SUSPICION";

	private final static String SEND_REQUEST_GLOBAL_VOTE_RESULTS = "SEND_REQUEST_GLOBAL_VOTE_RESULTS";
	private final static String RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS = "RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS";

	private final static String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final static String STATE_RESULTS = "RESULTS";
	private final static String STATE_SEND_RESULTS = "SEND_RESULTS";
	private String step;
	private String nextStep;

	private VoteRequest request;
	private IController controllerAgent;

	public SynchronousVoteBehaviour(IController controllerAgent) {		
		this.controllerAgent = controllerAgent;

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
					this.request.setLocalVoteResults(this.results);
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
					this.request.setGlobalCitizenVoteResults(this.globalResults);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				this.nextStep = STATE_GET_SIMPLE_SUSPICION;
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

				AID aidPlayer = message.getSender();

				this.results.add(res);

				if(this.request.getRequest().equals("CITIZEN_VOTE")&&
						DFServices.containsGameAgent(aidPlayer, "PLAYER", "MAYOR", this.myAgent, this.controllerAgent.getGameid()))
				{
					/** poids double **/
					this.results.add(res);
					this.globalResults.add(res);
				}
				
				
				//envoi du resultat partiel 
				String json = "";
				mapper = new ObjectMapper();
				try {
					json = mapper.writeValueAsString(this.results);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
				msg.setContent(json);
				msg.setSender(this.myAgent.getAID());
				msg.setConversationId("NEW_VOTE_RESULTS");

				List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", this.myAgent, this.controllerAgent.getGameid());
				if(!agents.isEmpty())
				{
					msg.addReceiver(agents.get(0));
					this.myAgent.send(msg);
				}
				
				System.out.println(""+this.nbVoters+"/"+this.request.getAIDVoters().size());

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
		
		
		else if(this.step.equals(STATE_GET_SIMPLE_SUSPICION))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.myAgent.getAID());
			messageRequest.setConversationId("GET_SIMPLE_SUSPICION");
			
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

			this.nextStep = STATE_ADD_SIMPLE_SUSPICION;
		}
		else if(this.step.equals(STATE_ADD_SIMPLE_SUSPICION))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("GET_SIMPLE_SUSPICION"));
			
			ACLMessage message = this.myAgent.receive(mt);
			if(message!=null)
			{
				++this.nbVoters;

				ObjectMapper mapper = new ObjectMapper();
				SuspicionScore suspicionScore = new SuspicionScore();
				try {
					suspicionScore = mapper.readValue(message.getContent(), SuspicionScore.class);
					System.err.println("JSON PARTIAL SUSPICION \n"+message.getContent());
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}

				AID aidPlayer = message.getSender();
				this.request.getCollectiveSuspicionScore().addSuspicionScoreGrid(aidPlayer.getName(), suspicionScore);

				if(this.nbVoters>= this.request.getAIDVoters().size())
				{
					this.nbVoters = 0;
					System.err.println("SUSPICION COLLECTIVE \n "+this.request.getCollectiveSuspicionScore().getScore());
					
					//sort random
					Collections.shuffle(this.request.getVoters());
					this.nextStep = STATE_SEND_REQUEST;
				}
				else
				{
					this.nextStep = STATE_GET_SIMPLE_SUSPICION;
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

					System.err.println("INTERBLOCAGE");

					/** random choice  **/
					tmp.add(this.finalResults.get((int)Math.random()*this.finalResults.size()));				
					this.finalResults = tmp;

					this.nextStep = STATE_SEND_RESULTS;

				}
				else
				{
					// new vote with finalists
					this.request.setChoices(this.finalResults);

					//sort random
					Collections.shuffle(this.request.getVoters());
					
					this.results = new VoteResults();
					this.request.setLocalVoteResults(this.results);
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

			
			//envoi resultat final + maj global vote
			if(this.request.getRequest().equals("CITIZEN_VOTE"))
			{
				String json = "";
				ObjectMapper mapper = new ObjectMapper();
				try {
					json = mapper.writeValueAsString(this.results);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				ACLMessage message = new ACLMessage(ACLMessage.AGREE);
				message.setContent(json);
				message.setSender(this.myAgent.getAID());
				message.setConversationId("NEW_CITIZEN_VOTE_RESULTS");

				List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", this.myAgent, this.controllerAgent.getGameid());
				if(!agents.isEmpty())
				{
					message.addReceiver(agents.get(0));
					this.myAgent.send(message);
				}
			}
			
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setSender(this.myAgent.getAID());
			message.addReceiver(this.agentSender);
			message.setConversationId("VOTE_RESULTS");
			message.setContent(this.finalResults.get(0));
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
