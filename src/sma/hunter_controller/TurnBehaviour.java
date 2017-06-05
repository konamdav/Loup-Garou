package sma.hunter_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;
import sma.model.VoteRequest;
import sma.werewolf_controller_agent.WerewolfControllerAgent;
import sun.java2d.pipe.SpanShapeRenderer.Simple;

public class TurnBehaviour extends SimpleBehaviour {

	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST = "RECEIVE_VOTE_REQUEST";
	private static final String STATE_SEND_ADD_VICTIM = "ADD_VICTIM";

	private HunterControllerAgent ctrlAgent;
	private AID aidVictim;
	private String step;
	private String nextStep;

	public TurnBehaviour(HunterControllerAgent HunterControllerAgent) {
		super(HunterControllerAgent);
		this.step = STATE_SEND_VOTE_REQUEST;
		this.ctrlAgent = HunterControllerAgent;
		this.nextStep = "";
	}

	public void action() {

		/** etat envoi requete demande de vote **/
		if (this.step.equals(STATE_SEND_VOTE_REQUEST)) {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("START_TURN"));
			ACLMessage message = this.myAgent.receive(mt);
			
			if (message != null) {
				List<String> choices = new ArrayList<String>();
				List<String> voters = new ArrayList<String>();

				String[] args = { Roles.HUNTER, Status.DEAD };
				List<AID> hunter = DFServices.findGamePlayerAgent(Roles.HUNTER, this.ctrlAgent, this.ctrlAgent.getGameid());

				List<AID> citizens = DFServices.findGamePlayerAgent(Roles.CITIZEN, this.ctrlAgent,
						this.ctrlAgent.getGameid());

				for (AID aid : citizens) {
					choices.add(aid.getName());
				}

				for (AID aid : hunter) {
					voters.add(aid.getName());
				}

				VoteRequest request = new VoteRequest();
				request.setVoteAgainst(true);
				request.setRequest("HUNTER_VOTE");
				request.setChoices(choices);
				request.setVoters(voters);
				System.out.println("voteur:"+voters.get(0));
				request.setCanBeFake(false);

				ObjectMapper mapper = new ObjectMapper();
				String json = "";
				try {
					json = mapper.writeValueAsString(request);
				} catch (IOException e) {
					e.printStackTrace();
				}

				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(this.ctrlAgent.getAID());
				messageRequest.setConversationId("VOTE_REQUEST");
				messageRequest.setContent(json);
				this.ctrlAgent.send(messageRequest);
				this.step = STATE_RECEIVE_VOTE_REQUEST;
				
			} else {
				block();
			}
		}
		/** etat reception du vote **/
		else if (this.step.equals(STATE_RECEIVE_VOTE_REQUEST)) {

			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_INFORM"));
			ACLMessage message = this.ctrlAgent.receive(mt);
			if (message != null) {
				
				String victim = message.getContent();
				aidVictim = new AID(victim);
				
				this.step = STATE_SEND_ADD_VICTIM;
			} else {
				 block();
			}
		}
		/** etat envoi des requ�tes de sommeil **/
		else if (this.step.equals(STATE_SEND_ADD_VICTIM)) {

			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("ADD_VICTIM");
			message.setContent(this.aidVictim.getName());
			message.setSender(this.ctrlAgent.getAID());

			List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				message.addReceiver(agents.get(0));
				this.ctrlAgent.send(message);
			}

			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("END_TURN");
			reply.addReceiver(this.myAgent.getAID());
			this.myAgent.send(reply);
			
			this.step = "";
		}
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
}
