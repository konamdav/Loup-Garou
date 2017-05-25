package sma.generic_death;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

public class AbstractDeathBehaviour extends CyclicBehaviour{
	private PlayerAgent agent;

	private String step;
	private String nextStep;

	private ArrayList<String> roles_death_answer; //modify name
	private Set<String> roles_behaviour_answer; //modify name

	private AID sender;
	
	private final static String STATE_WAIT_DEATH ="STATE_WAIT_DEATH";
	private final static String STATE_SEND_DEATH_ROLES ="STATE_SEND_DEATH_ROLES";
	private final static String STATE_WAIT_ALL_ANSWERS_DEATH_ROLES ="STATE_WAIT_ALL_ANSWERS_DEATH_ROLES";
	private final static String STATE_DELETE_ALL_BEHAVIOUR ="STATE_DELETE_ALL_BEHAVIOUR";
	private final static String STATE_WAIT_DELETE_ALL_BEHAVIOUR ="STATE_WAIT_DELETE_ALL_BEHAVIOUR";
	private final static String STATE_ANSWER_DEATH_ROLES ="STATE_ANSWER_DEATH_ROLES";

	public AbstractDeathBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
		this.nextStep = "";
		this.step = STATE_WAIT_DEATH;
		this.sender = null;
		this.roles_death_answer = null;
		this.roles_behaviour_answer = null;
	}

	/**
	 * Machine à état, 
	 * 1. Attends message de mort
	 * 2. Envoie messages à tous les behaviours de death
	 * 3. Attends messages de tous ces behaviours
	 * 4. Delete all the behaviour
	 * 5. Mets à jour le statut de cet agent et renvoie message de confirmation de mort
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	//TODO CEDRIC Check for a PreDeath for the ancien (Tu fais chier David)
	//TODO CEDRIC Delete the behaviour 

	@Override
	public void action() {
		
		if (this.step.equals(STATE_WAIT_DEATH)){

			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("KILL_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				System.err.println("[ "+this.agent.getName()+" ] DIE Begin ");
				this.sender = message.getSender();
				this.nextStep = STATE_SEND_DEATH_ROLES;
			}
			else
			{
				this.nextStep = "";
				block();
			}
		}
		else if (this.step.equals(STATE_SEND_DEATH_ROLES)){
			//Copy this technique from AbstractVoteBehvaiour
			//System.out.println("Send message to all death behaviours " + this.agent.getName() + " DeathBeahav " + this.agent.getDeathBehaviours() + "   " + this.step);

			this.roles_death_answer = new ArrayList<String>(this.agent.getDeathBehaviours());
			//TODO Look if better to pass to oneshotBehaviour
			for(String s : this.roles_death_answer)
			{
				//System.out.println("ROLE DEATH : "+s);

				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.agent.getAID());
				messageRequest.addReceiver(this.agent.getAID());
				messageRequest.setConversationId("DEATH_"+s+"_REQUEST");

				this.myAgent.send(messageRequest);
			}

			this.nextStep = STATE_WAIT_ALL_ANSWERS_DEATH_ROLES;
			//System.out.println("End  message to all death behaviours " + this.agent.getName() + "   " + this.step);
			
		}
		else if (this.step.equals(STATE_WAIT_ALL_ANSWERS_DEATH_ROLES)){
			
			if (this.roles_death_answer == null || this.roles_death_answer.isEmpty()){
				//System.out.println("All answer from death beahviour death ");
				this.nextStep = STATE_DELETE_ALL_BEHAVIOUR;
			}
			else {
				//System.out.println("Wait answer death  " + this.roles_death_answer);
				//Create tmp list, in order to be hable to remove roles_death_answer. 
				ArrayList<String> tmp = new ArrayList<String>(this.roles_death_answer);
				for (String s : tmp){
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate.MatchConversationId("DEATH_"+s+"_REQUEST"));
		
					ACLMessage message = this.myAgent.receive(mt);
					if (message != null) 
					{
						//System.out.println(" Receive Death Answer "+this.agent.getName()+" " + s);
						
						this.roles_death_answer.remove(s);
					}
				}
				this.nextStep = "";
				//Check if has to wait message
				if (this.roles_death_answer != null && this.roles_death_answer.isEmpty() == false)
					block();
			}
		}
		else if (this.step.equals(STATE_DELETE_ALL_BEHAVIOUR))
		{
			this.roles_behaviour_answer = this.agent.getMap_role_behaviours().keySet();
			//System.out.println(" Delete all behaviours " + this.agent.getName() + " roles : " + this.roles_behaviour_answer);

			for (String role : this.roles_behaviour_answer ){
				ACLMessage message = new ACLMessage(ACLMessage.CANCEL);
				message.setConversationId("DELETE_ROLE");
				message.setContent(role);
				this.agent.send(message);
			}

			this.nextStep = STATE_WAIT_DELETE_ALL_BEHAVIOUR;
		}
		else if (this.step.equals(STATE_WAIT_DELETE_ALL_BEHAVIOUR))
		{
			if (this.roles_death_answer == null || this.roles_death_answer.isEmpty()){
				//System.out.println("All answer from death beahviour death So destroy last behaviour ");
				
				//TODO Cedric Voir les behaviour à garder avec le beahviour removeBehaviour 
			
				//this.agent.getVotingBehaviours().clear();
				this.agent.getDeathBehaviours().clear();
				//Delete the generic behaviour from this beahviour
				for (Behaviour b : this.agent.getMap_role_behaviours().get(Roles.GENERIC)){
					this.agent.removeBehaviour(b);
				}
				this.nextStep = STATE_ANSWER_DEATH_ROLES;
			}
			else {
				//System.out.println(" Wailt all delete behaviours reponse " + this.agent.getName());
				ArrayList<String> tmp = new ArrayList<String>(this.roles_behaviour_answer);
				//Create tmp list, in order to be hable to remove roles_death_answer. 

				for (String s : tmp){
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate.MatchConversationId("DELETE_ROLE"+s));
		
					ACLMessage message = this.myAgent.receive(mt);
					if (message != null) 
					{
						this.roles_behaviour_answer.remove(s);
					}
				}
				this.nextStep = "";
				//Check if has to wait message
				if (this.roles_behaviour_answer != null && this.roles_behaviour_answer.isEmpty() == false)
					block();
			}
		}
		else if (this.step.equals(STATE_ANSWER_DEATH_ROLES))
		{
			System.err.println("[ "+this.agent.getName()+" ] DIE End ");
			//Answer 
			this.agent.setStatutandRegister("DEAD");
			DFServices.deregisterPlayerAgent("VICTIM", this.myAgent, this.agent.getGameid()); //retire son statut de victime (car il est mort)

			ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
			reply.setConversationId("DEAD_PLAYER");
			reply.setSender(this.myAgent.getAID());
			reply.addReceiver(this.sender);

			this.myAgent.send(reply);
			
			this.agent.removeBehaviour(this);
		}
		
		
		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}
}


