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

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Functions;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.Status;
import sma.model.VoteRequest;
import sma.model.VoteResults;

import sma.player_agent.PlayerAgent;

public class AbstractDeathBehaviour extends CyclicBehaviour{
	private PlayerAgent agent;

	private String step;
	private String nextStep;

	private ArrayList<String> roles_death_answer; //modify name
	private ArrayList<String> roles_behaviour_answer; //modify name
	private ArrayList<String> roles_pre_death_answer; //modify name
	
	private AID sender;
	
	private final static String STATE_WAIT_DEATH ="STATE_WAIT_DEATH";
	private final static String STATE_SEND_PRE_DEATH_ROLES ="STATE_SEND_PRE_DEATH_ROLES";
	private final static String STATE_WAIT_ALL_ANSWERS_PRE_DEATH_ROLES ="STATE_WAIT_ALL_ANSWERS_PRE_DEATH_ROLES";
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
	//TODO CEDRIC Check for a PreDeath for the ancien 
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
				//System.err.println("[ "+this.agent.getName()+" ] DIE Begin ");
				this.sender = message.getSender();
				this.nextStep = STATE_SEND_PRE_DEATH_ROLES;
				
			}
			else
			{
				this.nextStep = "";
				block(1000);
			}
		}
		else if (this.step.equals(STATE_SEND_PRE_DEATH_ROLES)){
			this.roles_pre_death_answer = new ArrayList<String>(this.agent.getPreDeathBehaviours());

			//Pre Death, if multiple pre_death
			//So one by one, if cancel have to cancel other pre death
			//So have to add message cancel in PreDeath behaviour which has to cancel what he has just done
			for(String s : this.roles_pre_death_answer)
			{
				//System.out.println("ROLE DEATH : "+s);
				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.agent.getAID());
				messageRequest.addReceiver(this.agent.getAID());
				messageRequest.setConversationId("PRE_DEATH_"+s+"_REQUEST");
				this.myAgent.send(messageRequest);
			}
			//System.out.println("End  message to all death behaviours " + this.agent.getName() + "   " + this.step);
			this.nextStep = STATE_WAIT_ALL_ANSWERS_PRE_DEATH_ROLES;
			
			
		}
		else if (this.step.equals(STATE_WAIT_ALL_ANSWERS_PRE_DEATH_ROLES)){
			if (this.roles_pre_death_answer == null || this.roles_pre_death_answer.isEmpty()){
				//System.out.println("All answer from pre_death beahviour death ");
				this.nextStep = STATE_SEND_DEATH_ROLES;
			}
			else {
				//System.out.println("Wait answer death  " + this.roles_death_answer);
				//Create tmp list, in order to be hable to remove roles_death_answer. 
				ArrayList<String> tmp = new ArrayList<String>(this.roles_pre_death_answer);
				for (String s : tmp){
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate.MatchConversationId("PRE_DEATH_"+s+"_REQUEST"));
		
					ACLMessage message = this.myAgent.receive(mt);
					if (message != null) 
					{
						//System.out.println(" Receive Death Answer CONFIRM "+this.agent.getName()+" " + s);
						this.roles_pre_death_answer.remove(s);
					}
					else {
						mt = MessageTemplate.and(
								MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
								MessageTemplate.MatchConversationId("PRE_DEATH_"+s+"_REQUEST"));
						message = this.myAgent.receive(mt);
						if (message != null) 
						{
							//System.out.println(" Receive Death Answer CANCEL "+this.agent.getName()+" Return to first state " + s);
							DFServices.deregisterPlayerAgent("VICTIM", this.myAgent, this.agent.getGameid()); //retire son statut de victime (car il est mort)

							ACLMessage reply = new ACLMessage(ACLMessage.CANCEL);
							reply.setConversationId("DEAD_PLAYER");
							reply.setSender(this.myAgent.getAID());
							reply.addReceiver(this.sender);

							this.myAgent.send(reply);
							
							this.nextStep = STATE_WAIT_DEATH;
							break; //Stop the for
							
						}
					}
				}
				if (this.nextStep != STATE_WAIT_DEATH)
					this.nextStep = "";
				//Check if has to wait message
				if (this.roles_pre_death_answer != null && this.roles_pre_death_answer.isEmpty() == false)
					block(1000);
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
					block(1000);
			}
		}
		else if (this.step.equals(STATE_DELETE_ALL_BEHAVIOUR))
		{
			this.roles_behaviour_answer = new ArrayList<String>(this.agent.getMap_role_behaviours().keySet());
			this.roles_behaviour_answer.remove(Roles.GENERIC);
			System.out.println(" Delete all behaviours " + this.agent.getName() + " roles : " + this.roles_behaviour_answer);

			for (String role : this.roles_behaviour_answer ){
				ACLMessage message = new ACLMessage(ACLMessage.CANCEL);
				message.setConversationId("DELETE_BEHAVIOUR");
				message.setContent(role);
				message.addReceiver(this.agent.getAID());
				this.agent.send(message);
			}

			this.nextStep = STATE_WAIT_DELETE_ALL_BEHAVIOUR;
		}
		else if (this.step.equals(STATE_WAIT_DELETE_ALL_BEHAVIOUR))
		{
			if (this.roles_behaviour_answer == null || this.roles_behaviour_answer.isEmpty()){
				//System.out.println("All answer from death beahviour death So destroy last behaviour ");
				//System.out.println(" roles_behaviour_answer empty " + this.agent.getName());

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
				System.out.println(" roles_behaviour_answer to delete " + this.agent.getName() + " delete " + this.roles_behaviour_answer);

				for (String s : tmp){
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate.MatchConversationId("DELETE_BEHAVIOUR"+s));
		
					ACLMessage message = this.myAgent.receive(mt);
					if (message != null) 
					{
						this.roles_behaviour_answer.remove(s);
					}
				}
				this.nextStep = "";
				//Check if has to wait message
				if (this.roles_behaviour_answer != null && this.roles_behaviour_answer.isEmpty() == false)
					block(1000);
			}
		}
		else if (this.step.equals(STATE_ANSWER_DEATH_ROLES))
		{
			//System.err.println("[ "+this.agent.getName()+" ] DIE End ");
			//Answer 
			System.err.println(this.agent.getStatut() + "   AID " + this.agent.getAID());
			//System.err.println(DFServices.findGamePlayerAgent("DEAD", this.myAgent, this.agent.getGameid()));
			
			DFServices.deregisterPlayerAgent("VICTIM", this.myAgent, this.agent.getGameid()); //retire son statut de victime (car il est mort)

			this.agent.setStatutandRegister(Status.DEAD);			
			
			if(this.agent.isHuman()){
				Functions.decHumans(this.agent, this.agent.getGameid());
			}
			//System.err.println(this.agent.getStatut());


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


