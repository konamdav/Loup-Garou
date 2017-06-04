package sma.voleur_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.ForceVoteRequest;
import sma.model.Roles;
import sma.model.Status;
import sma.model.VoteRequest;

/***
 * Behaviour de gestion du tour des citizens
 * @author Davy
 *
 */
public class TurnBehaviour extends SimpleBehaviour {
	private final String STATE_INIT = "INIT";
	private final String STATE_WAITING_START = "WAITING_START";
	private final String STATE_END_TURN = "END_TURN";
	private final String STATE_SEND_WAKE_ONE = "SEND_WAKE_ONE";
	private final String STATE_RECEIVE_WAKE_ONE = "RECEIVE_WAKE_ONE";
	
	private final String STATE_SEND_VOTE_REQUEST = "SEND_VOTE_REQUEST";
	private final String STATE_RECEIVE_VOTE_REQUEST= "RECEIVE_VOTE_REQUEST";

	private final String STATE_SEND_GET_ROLE_REQUEST= "SEND_GET_ROLE_REQUEST";
	private final String STATE_RECEIVE_GET_ROLE_REQUEST= "RECEIVE_GET_ROLE_REQUEST";

	private final String STATE_SEND_DELETE_ROLE_REQUEST= "SEND_DELETE_ROLE_REQUEST";
	private final String STATE_RECEIVE_DELETE_ROLE_REQUEST= "RECEIVE_DELETE_ROLE_REQUEST";
	
	private final String STATE_SEND_CHANGE_ROLE = "SEND_CHANGE_ROLE";
	private final String STATE_RECEIVE_CHANGE_ROLE = "RECEIVE_CHANGE_ROLE";

	private final String STATE_SEND_SLEEP_ONE = "SEND_SLEEP_ONE";
	private final String STATE_RECEIVE_SLEEP_ONE = "RECEIVE_SLEEP_ONE";

	//private final String STATE_FIX_RESULT = "FIX_RESULT"; TODO Cedric test it


	private String step;
	private String nextStep;

	private AID current_volleur;
	private String new_role;
	private int cpt_vol;

	private VoleurControllerAgent ctrlAgent;

	private AID aidChosen;

	public TurnBehaviour(VoleurControllerAgent FamilyControllerAgent) {
		super(FamilyControllerAgent);

		this.ctrlAgent = FamilyControllerAgent;
		this.step = STATE_INIT;
		this.nextStep ="";
		this.current_volleur = null;
		this.cpt_vol = 0;
	}

	@Override
	public void action() {

		System.out.println("CC "+this.step);

		/** etat initial **/
		if(this.step.equals(STATE_INIT))
		{
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("*******************************************");

			this.nextStep = STATE_WAITING_START;
		}
		/** etat d'attente de d�but de tour **/
		else if(this.step.equals(STATE_WAITING_START))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("START_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_WAKE_ONE;
			}
			else
			{
				block();
			}

		}
		/** etat envoi des requ�tes de reveil pour un joueur **/
		else if(this.step.equals(STATE_SEND_WAKE_ONE))
		{
			String[] args ={Status.SLEEP, Roles.VOLEUR};
			List<AID> agents = DFServices.findGamePlayerAgent( args , this.ctrlAgent, this.ctrlAgent.getGameid());

			//this.nbPlayers = agents.size(); TODO CEDRIC
			if (agents.isEmpty() == false){
				this.cpt_vol = 0 ; //Cpt to know if role init or delete of the volé et le voleur
				this.current_volleur = agents.get(0);
				System.err.println("Launch voleur turn for " + this.current_volleur);

				ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
				messageRequest.setSender(this.ctrlAgent.getAID());
				messageRequest.addReceiver(this.current_volleur);
				messageRequest.setConversationId("WAKE_PLAYER");
				this.myAgent.send(messageRequest);
				this.nextStep = STATE_RECEIVE_WAKE_ONE;
			}
			else {
				System.err.println("Fin du tour voleur");
				this.nextStep = STATE_END_TURN;

			}

		}
		/** etat reception des confirmations de reveil **/
		else if(this.step.equals(STATE_RECEIVE_WAKE_ONE))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("WAKE_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.nextStep = STATE_SEND_VOTE_REQUEST;
			}
			else
			{
				block();
			}
		}

		/** etat envoi requete demande de vote **/
		else if(this.step.equals(STATE_SEND_VOTE_REQUEST))
		{
			List<String> choices = new ArrayList<String>();
			List<String> voters = new ArrayList<String>();

			/*String [] args = {Roles.VOLEUR, Status.WAKE};
			List<AID> cupids = DFServices.findGamePlayerAgent(args, this.ctrlAgent, this.ctrlAgent.getGameid());

			this.nbPlayers = cupids.size();*/

			String [] args2 = {Roles.CITIZEN, Status.SLEEP};
			List<AID> citizens = DFServices.findGamePlayerAgent(args2, this.ctrlAgent, this.ctrlAgent.getGameid());
			citizens.remove(this.current_volleur);

			for(AID aid : citizens)
			{
				choices.add(aid.getName());
			}

			voters.add(this.current_volleur.getName());

			VoteRequest request = new VoteRequest();
			request.setVoteAgainst(true);
			request.setRequest("VOLEUR_VOTE"); //TODO CEDRIC TEST IT
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


			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.ctrlAgent.getAID());
			messageRequest.setConversationId("VOTE_REQUEST");
			messageRequest.setContent(json);
			this.ctrlAgent.send(messageRequest);
			this.nextStep = STATE_RECEIVE_VOTE_REQUEST;
		}

		/** etat reception du vote **/
		else if(this.step.equals(STATE_RECEIVE_VOTE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("VOTE_RESULTS"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String chosen = message.getContent();
				this.aidChosen = new AID(chosen);
				this.nextStep = STATE_SEND_GET_ROLE_REQUEST;
			}
			else
			{
				block();
			}
		}
		
		/** Envoie de demande du nouveau role **/
		else if(this.step.equals(STATE_SEND_GET_ROLE_REQUEST))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.aidChosen);
			messageRequest.setConversationId("GET_ROLE");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_GET_ROLE_REQUEST;
		}
		/** Reception du nouveau role **/
		else if(this.step.equals(STATE_RECEIVE_GET_ROLE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("GET_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				String chosen = message.getContent();
				this.new_role = chosen;

				this.nextStep = STATE_SEND_DELETE_ROLE_REQUEST;
			}
			else
			{
				block();
			}
		}
		
		/** Demande de suppression role du voleur et du volé  **/
		else if(this.step.equals(STATE_SEND_DELETE_ROLE_REQUEST))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.CANCEL);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.setContent(this.new_role); //Delete this role
			messageRequest.addReceiver(this.aidChosen);
			messageRequest.setConversationId("DELETE_ROLE");
			this.myAgent.send(messageRequest);
			
			ACLMessage messageRequest2 = new ACLMessage(ACLMessage.CANCEL);
			messageRequest2.setSender(this.ctrlAgent.getAID());
			messageRequest2.addReceiver(this.current_volleur);
			messageRequest2.setContent(Roles.VOLEUR); //Delete the role of the voleur
			messageRequest2.setConversationId("DELETE_ROLE");
			this.myAgent.send(messageRequest2);

			this.nextStep = STATE_RECEIVE_DELETE_ROLE_REQUEST;
		}
		/** Reception de la suppresion le de l'autre role **/
		else if(this.step.equals(STATE_RECEIVE_DELETE_ROLE_REQUEST))
		{

			if (this.cpt_vol == 2 )
			{
				this.nextStep = STATE_SEND_CHANGE_ROLE;
			}
			else {
				/*** reception role supprimé du voleur**/
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
						MessageTemplate.MatchConversationId("DELETE_ROLE"+Roles.VOLEUR));

				ACLMessage message = this.myAgent.receive(mt);
				if(message != null)
				{
					this.cpt_vol++;
					//this.nextStep = STATE_RECEIVE_DELETE_ROLE_REQUEST; //Do the same turn again
				}
				else {
					MessageTemplate mt2 = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate.MatchConversationId("DELETE_ROLE"+this.new_role));

					ACLMessage message2 = this.myAgent.receive(mt2);
					if(message2 != null)
					{
						this.cpt_vol++;
						//this.nextStep = STATE_RECEIVE_DELETE_ROLE_REQUEST; //Do the same turn again
					}
					else
					{
						block();
					}
				}
			}
		}
		
		/** Demande de changement de rôle **/
		else if(this.step.equals(STATE_SEND_CHANGE_ROLE))
		{
			/** msg attribution role **/
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.aidChosen);
			messageRequest.setConversationId("ATTRIBUTION_ROLE");
			messageRequest.setContent(Roles.CITIZEN);
			this.myAgent.send(messageRequest);
			//DFServices.registerPlayerAgent(Roles.CITIZEN, this.aidChosen, this.ctrlAgent.getGameid()); //Have to register him


			ACLMessage messageRequest2 = new ACLMessage(ACLMessage.REQUEST);
			messageRequest2.setSender(this.ctrlAgent.getAID());
			messageRequest2.addReceiver(this.current_volleur);
			messageRequest2.setConversationId("ATTRIBUTION_ROLE");
			messageRequest2.setContent(this.new_role);
			this.myAgent.send(messageRequest2);
			
			//if(this.new_role.equals(Roles.CITIZEN))
			//	DFServices.registerPlayerAgent(Roles.CITIZEN, this.myAgent, this.ctrlAgent.getGameid()); //Have to register him
			
			this.cpt_vol = 0; //Init this cpt for next states
			
			this.nextStep = STATE_RECEIVE_CHANGE_ROLE;
		}
		/** Reception des changements de rôle **/
		else if(this.step.equals(STATE_RECEIVE_CHANGE_ROLE))
		{
			if (this.cpt_vol == 2 )
			{
				this.nextStep = STATE_SEND_SLEEP_ONE; //STATE_SEND_WAKE_ONE
			}
			else {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("ATTRIBUTION_ROLE"));

				ACLMessage message = this.myAgent.receive(mt);
				if(message != null)
				{
					this.cpt_vol++;
				}
				else
				{
					block();
				}				
			}
		}

		/*else if(this.step.equals(STATE_SEND_SLEEP_ONE))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.current_volleur);
			messageRequest.setConversationId("SLEEP_PLAYER");
			this.myAgent.send(messageRequest);
			
			ACLMessage messageRequest2 = new ACLMessage(ACLMessage.REQUEST);
			messageRequest2.setSender(this.ctrlAgent.getAID());
			messageRequest2.addReceiver(this.aidChosen);
			messageRequest2.setConversationId("SLEEP_PLAYER");
			this.myAgent.send(messageRequest2);

			this.cpt_vol = 0;

			this.nextStep = STATE_RECEIVE_SLEEP_ONE;
		}
		else if(this.step.equals(STATE_RECEIVE_SLEEP_ONE))
		{
			if (this.cpt_vol == 2 ){
				//Init all variables
				this.cpt_vol = 0;
				this.current_volleur = null;
				this.aidChosen = null;
				
				this.nextStep = STATE_SEND_WAKE_ONE; //Return at the begin, to check if other voleur
			}
			else {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
						MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

				ACLMessage message = this.myAgent.receive(mt);
				if(message != null)
				{
					this.cpt_vol++;
				}
				else
				{
					block();
				}
			}

		}*/
		/** etat envoi une requ�te de sommeil **/
		else if(this.step.equals(STATE_SEND_SLEEP_ONE))
		{
			ACLMessage messageRequest = new ACLMessage(ACLMessage.REQUEST);
			messageRequest.setSender(this.ctrlAgent.getAID());
			messageRequest.addReceiver(this.current_volleur);
			messageRequest.setConversationId("SLEEP_PLAYER");
			this.myAgent.send(messageRequest);

			this.nextStep = STATE_RECEIVE_SLEEP_ONE;
		}
		/** reception des confirmations de sommeil **/
		else if(this.step.equals(STATE_RECEIVE_SLEEP_ONE))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchConversationId("SLEEP_PLAYER"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				//Init all variables
				this.cpt_vol = 0;
				this.current_volleur = null;
				this.aidChosen = null;
				
				this.nextStep = STATE_SEND_WAKE_ONE; //Return at the begin, to check if other voleur
			}
			else
			{
				block();
			}
		}
		/** etat fin de tour **/
		else if(this.step.equals(STATE_END_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent("GAME", this.ctrlAgent, this.ctrlAgent.getGameid());
			if(!agents.isEmpty())
			{
				ACLMessage  message = new ACLMessage(ACLMessage.INFORM);
				message.setConversationId("END_TURN");
				message.setSender(this.ctrlAgent.getAID());
				message.addReceiver(agents.get(0));
				this.ctrlAgent.send(message);
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
