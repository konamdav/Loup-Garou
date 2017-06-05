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
	private AID aidHunter = null;

	public TurnBehaviour(HunterControllerAgent HunterControllerAgent) {
		super(HunterControllerAgent);
		this.step = STATE_SEND_VOTE_REQUEST;
		this.ctrlAgent = HunterControllerAgent;
	}

	public void action() {		
		/** etat envoi requete demande de vote **/
		if (this.step.equals(STATE_SEND_VOTE_REQUEST)) {
			
			this.aidHunter = null;
			this.aidVictim = null;
			
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("START_TURN"));
			ACLMessage message = this.myAgent.receive(mt);
			
			if (message != null) {
				
				this.aidHunter = message.getSender();
				List<String> choices = new ArrayList<String>();
				List<String> voters = new ArrayList<String>();
				
				//choix parmis les vivants
				String [] args = {Roles.CITIZEN, Status.WAKE};
				List<AID> citizens = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

				for (AID aid : citizens) {
					choices.add(aid.getName());
				}
				//un seul hunter
				voters.add(this.aidHunter.getName());

				//creation requete
				VoteRequest request = new VoteRequest();
				request.setVoteAgainst(true);
				request.setRequest("HUNTER_VOTE");
				request.setChoices(choices);
				request.setVoters(voters);
				request.setCanBeFake(false);

				ObjectMapper mapper = new ObjectMapper();
				String json = "";
				try {
					json = mapper.writeValueAsString(request);
				} catch (IOException e) {
					e.printStackTrace();
				}

				//envoi rqst
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
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("VOTE_RESULTS"));
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

			//ajout victime
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId("ADD_VICTIM");
			message.setContent(this.aidVictim.getName());
			message.setSender(this.ctrlAgent.getAID());

			List<AID> agents = DFServices.findGameControllerAgent(Roles.CITIZEN, this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				message.addReceiver(agents.get(0));
				this.ctrlAgent.send(message);
			}

			//fin de tour
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.setConversationId("END_TURN");
			reply.addReceiver(this.aidHunter);
			this.myAgent.send(reply);			
			
			
			//on reset le controlleur pour qu'il serve a un autre chasseur
			this.step = STATE_SEND_VOTE_REQUEST;
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}
