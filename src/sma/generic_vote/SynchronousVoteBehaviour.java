package sma.generic_vote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.data.Data;
import sma.generic.interfaces.IController;
import sma.model.DFServices;
import sma.model.Functions;
import sma.model.Roles;
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

	private int nbAsynchronousPlayers;
	private AID agentSender;
	private final String STATE_INIT = "INIT";
	private final String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final String STATE_SEND_REQUEST = "SEND_REQUEST";

	private final String STATE_GET_SIMPLE_SUSPICION = "GET_SIMPLE_SUSPICION";
	private final String STATE_ADD_SIMPLE_SUSPICION = "ADD_SIMPLE_SUSPICION";

	private final String SEND_REQUEST_GLOBAL_VOTE_RESULTS = "SEND_REQUEST_GLOBAL_VOTE_RESULTS";
	private final String RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS = "RECEIVE_REQUEST_GLOBAL_VOTE_RESULTS";

	private final String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final String STATE_RESULTS = "RESULTS";
	private final String STATE_SEND_RESULTS = "SEND_RESULTS";
	private String step;
	private String nextStep;

	private VoteRequest request;
	private IController controllerAgent;
	private long currentTime;

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

		//System.out.println("STATE SV = "+this.step);

		if(this.step.equals(STATE_INIT))
		{
			this.nbVoters = 0;
			this.nbAsynchronousPlayers = 0;
			this.globalResults = new VoteResults();
			this.agentSender = null;
			this.request = null;
			this.currentTime = -1;
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

					//System.err.println("[RQST INITIAL] "+message.getContent());

					this.request = mapper.readValue(message.getContent(), VoteRequest.class);
					this.request.setLocalVoteResults(this.results);
					this.results.initWithChoice(this.request.getChoices());
					
					Functions.newActionToLog("Début vote "+this.request.getRequest(),this.myAgent, this.controllerAgent.getGameid());
					
				} catch (IOException e) {
					e.printStackTrace();
				}

				this.nextStep = SEND_REQUEST_GLOBAL_VOTE_RESULTS;
			}
			else
			{
				block(1000);
			}

		}
		else if(this.step.equals(SEND_REQUEST_GLOBAL_VOTE_RESULTS))
		{
			List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setSender(this.myAgent.getAID());
				for(AID aid : agents)
				{
					message.addReceiver(aid);
				}
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

				//System.out.println("CTRL Reception de global results");
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
				block(1000);
			}
		}		
		else if(this.step.equals(STATE_SEND_REQUEST))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.myAgent.getAID());
			messageRequest.setConversationId("VOTE_REQUEST");

			int reste = this.request.getAIDVoters().size()-this.nbVoters;
			if(false )// reste >= Data.MAX_SYNCHRONOUS_PLAYERS)
			{
				/** hybrid synchronous mode **/
				this.nbAsynchronousPlayers = (int) (Math.random()*Math.min(Data.MAX_SYNCHRONOUS_PLAYERS-1, (reste-1)))+1;
				//System.err.println("HYDBRID ASYNCRHONOUS ENABLED BECAUSE TOO MANY PARTICIPANTS "+this.nbAsynchronousPlayers+" in //");
			}
			else
			{
				this.nbAsynchronousPlayers = 1;
			}

			ObjectMapper mapper = new ObjectMapper();

			String json ="";

			try {		
				json = mapper.writeValueAsString(this.request);	
				System.err.println("[RQST] "+json);
			} catch (Exception e) {
				e.printStackTrace();
			}

			messageRequest.setContent(json);
			for(int i = 0; i<this.nbAsynchronousPlayers; ++i)
			{
				//System.err.println("DK ENVOI REQUEST "+this.request.getAIDVoters().get(this.nbVoters+i).getLocalName());
				messageRequest.addReceiver(this.request.getAIDVoters().get(this.nbVoters+i));	
			}

			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_INFORM;
		}
		else if(this.step.equals(STATE_RECEIVE_INFORM))
		{
			if(this.currentTime == -1)
			{
				this.currentTime = System.currentTimeMillis();
			}

			if(System.currentTimeMillis() - this.currentTime > 3000)
			{
				this.currentTime = -1;
				ACLMessage wakeup = new ACLMessage(ACLMessage.UNKNOWN);
				wakeup.setSender(this.myAgent.getAID());
				if(nbVoters < this.request.getVoters().size())
				{
					wakeup.addReceiver(this.request.getAIDVoters().get(nbVoters));
					wakeup.setConversationId("WAKEUP");
					this.myAgent.send(wakeup);
					
					System.out.println("Relance du joueur "+this.request.getAIDVoters().get(nbVoters).getLocalName()+" ...");
				}

				
			}

			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_INFORM"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message!=null)
			{
				this.currentTime = -1;
				++this.nbVoters;
				--this.nbAsynchronousPlayers;

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
					for(AID aid : agents)
					{
						msg.addReceiver(aid);
					}
					this.myAgent.send(msg);
				}

			///	System.err.println("\nSV : "+this.nbVoters+"/"+this.request.getAIDVoters().size());

				if(this.nbVoters >= this.request.getAIDVoters().size())
				{
					this.nbVoters = 0;
					//System.err.println("SV next step");
					this.nextStep = STATE_RESULTS;
				}
				else if(this.nbAsynchronousPlayers > 0)
				{
					//System.err.println("SV waiting other");
					this.nextStep = STATE_RECEIVE_INFORM;
				}
				else 
				{
					System.err.println("SV send request to other");
					this.nextStep = STATE_SEND_REQUEST;
				}
			}
			else
			{
				block(1000);	
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
			for(AID aid : this.request.getAIDVoters()){
				messageRequest.addReceiver(aid);	
			}

			this.nbVoters = 0;
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
					//System.err.println("ADD PARTIAL SUSPICION \n"+message.getContent());
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
					this.nextStep = STATE_ADD_SIMPLE_SUSPICION;
				}
			}
			else
			{
				this.nextStep = STATE_ADD_SIMPLE_SUSPICION;
				block(1000);
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
				//defavorisé le bouc emmissaire
				List<AID> scapegoats = DFServices.findGamePlayerAgent(Roles.SCAPEGOAT, this.myAgent, this.controllerAgent.getGameid());
				List<String> nameScapegoats = new ArrayList<String>();
				for(AID scapegoat : scapegoats)
				{
					nameScapegoats.add(scapegoat.getName());
				}
				for(String scapegoat : nameScapegoats)
				{
					if(this.finalResults.contains(scapegoat))
					{
						if(this.request.isVoteAgainst())
						{
							this.finalResults = nameScapegoats;
						}
						else if(this.finalResults.size()>1)
						{
							this.finalResults.remove(scapegoat);
						}

					}
				}

				if(this.lastResults != null && this.lastResults.equals(this.finalResults))
				{
					/** interblocage **/
					ArrayList<String> tmp = new ArrayList<String>();

					//System.err.println("INTERBLOCAGE");

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
					this.results.initWithChoice(this.request.getChoices());
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
					for(AID aid : agents)
					{
						message.addReceiver(aid);
					}
					this.myAgent.send(message);
				}
			}

			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setSender(this.myAgent.getAID());
			message.addReceiver(this.agentSender);
			message.setConversationId("VOTE_RESULTS");
			message.setContent(this.finalResults.get(0));
			
			Functions.newActionToLog("Vote : "+this.finalResults.get(0), this.getAgent(), this.controllerAgent.getGameid());
			
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
