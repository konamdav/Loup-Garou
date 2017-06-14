package sma.generic_vote;

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
import sma.model.VoteRequest;
import sma.model.VoteResults;

/** 
 * Vote asynchrone (les players répondent sans attendre les autres)
 * @author Davy
 *
 */
public class AsyncrhonousVoteBehaviour extends Behaviour {
	private int nbVoters;
	private VoteResults results;
	private VoteResults globalResults;
	private List<String> lastResults;
	private List<String> finalResults;
	private List<String> choices;

	private final String STATE_INIT = "INIT";
	private final String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final String STATE_SEND_REQUEST = "SEND_REQUEST";
	private final String STATE_RECEIVE_INFORM = "RECEIVE_INFORM";
	private final String STATE_RESULTS = "RESULTS";
	private final String STATE_SEND_RESULTS = "SEND_RESULTS";
	private String step;
	private String nextStep;

	public AsyncrhonousVoteBehaviour(CitizenControllerAgent citizenControllerAgent) {
		super(citizenControllerAgent);
		this.nbVoters = 0;
		this.results = new VoteResults();
		this.globalResults = null;
		this.lastResults = null;
		this.finalResults = null;

		this.step = STATE_INIT;
		this.nextStep ="";

	}


	public List<AID> getVoters()
	{
		ArrayList<AID> voters = new ArrayList<AID>();

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CITIZEN");
		sd.setName("CITIZEN");
		template.addServices(sd);
		try {
			DFAgentDescription[] result =
					DFService.search(this.myAgent, template);
			for(DFAgentDescription dfa: result)
			{
				voters.add(dfa.getName());
			}

		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return voters;
	}

	@Override
	public void action() {

		//System.out.println("STATE = "+this.step);

		if(this.step.equals(STATE_INIT))
		{
			this.nbVoters = 0;

			/** test **/
			choices = new ArrayList<String>();
			choices.add("Player1");
			choices.add("Player2");
			choices.add("Player3");
			choices.add("Player4");

			this.globalResults = new VoteResults();
			
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
				this.nextStep = STATE_SEND_REQUEST;
			}
			else
			{
				block(1000);
			}

		}
		else if(this.step.equals(STATE_SEND_REQUEST))
		{			
			for(AID aid : this.getVoters())
			{
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.myAgent.getAID());
				messageRequest.addReceiver(aid);
				messageRequest.setConversationId("VOTE_REQUEST");

				VoteRequest request = new VoteRequest(choices, this.globalResults);
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

				if(this.nbVoters>= this.getVoters().size())
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
					choices = this.finalResults;
					
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
