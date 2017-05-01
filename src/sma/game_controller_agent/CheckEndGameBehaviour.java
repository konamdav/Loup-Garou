package sma.game_controller_agent;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.Status;

/**
 * Behaviour qui prévient la fin de jeu
 * @author Davy
 *
 */
public class CheckEndGameBehaviour extends CyclicBehaviour {
	private GameControllerAgent gameControllerAgent;
	private final static String STATE_INIT = "INIT";
	private final static String STATE_RECEIVE_REQUEST = "RECEIVE_REQUEST";
	private final static String STATE_SEND_ANSWER = "SEND_ANSWER";
	private final static String STATE_NOTIFY_END_GAME = "NOTIFY_END_GAME";
	private final static String STATE_END = "END";
	
	
	
	private String step;
	private String nextStep;
	private AID sender;
	
	public CheckEndGameBehaviour(GameControllerAgent a) {
		super(a);
		this.gameControllerAgent = a;
		
		this.step = STATE_INIT;
		this.nextStep ="";
	
	}

	@Override
	public void action() {

		System.out.println("CHECK | "+this.step);
		
		if(this.step.equals(STATE_INIT))
		{
			this.sender = null;
			this.nextStep = STATE_RECEIVE_REQUEST;
		}
		else if (this.step.equals(STATE_RECEIVE_REQUEST))
		{
			/*** reception demande de vote **/
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("CHECK_END_GAME"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				System.out.println("!!!!!!!!!!!RECEIVE!!!!!!!!!!!!!!!!!!!");
				this.sender = message.getSender();
				this.nextStep = STATE_SEND_ANSWER;
				System.out.println("!!!!!!!!!!!RECEIVE!!!!!!!!!!!!!!!!!!!");
			}
			else
			{
				block();
			}
		}
		else if(this.step.equals(STATE_SEND_ANSWER))
		{
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setSender(this.gameControllerAgent.getAID());
			message.addReceiver(this.sender);
			
			String[] services1 = {Roles.WEREWOLF, Status.WAKE};
			List<AID> werewolves = DFServices.findGamePlayerAgent(services1, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			
			String[] services2 = {Roles.CITIZEN, Status.WAKE};
			List<AID> citizens = DFServices.findGamePlayerAgent(services2, this.gameControllerAgent, this.gameControllerAgent.getGameid());

			String[] services3 = {Roles.LOVER, Status.WAKE};
			List<AID> lovers = DFServices.findGamePlayerAgent(services3, this.gameControllerAgent, this.gameControllerAgent.getGameid());

			
			if( werewolves.isEmpty()) 
			{
				System.out.println("CHECK ? GAME ENDS");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
				
			}
			else if(werewolves.size() == citizens.size()) 
			{
				System.out.println("CHECK ? GAME ENDS");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}
			else if(lovers.size() == citizens.size()) 
			{
				System.out.println("CHECK ? GAME ENDS");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}	
			else
			{
				System.out.println("CHECK ? GAME CONTINUES");
				message.setConversationId("CONTINUE_GAME");
				this.nextStep = STATE_END;
			}
			
			this.gameControllerAgent.send(message);
		}
		else if(this.step.equals(STATE_NOTIFY_END_GAME))
		{
			System.out.println("NOTIFCATION");
			this.nextStep = STATE_END;
		}
		else if(this.step.equals(STATE_END))
		{
			this.nextStep = STATE_INIT;
		}
		
		
		if(!this.nextStep.isEmpty())
		{
			System.out.println("CHECK | next "+this.nextStep);
			this.step = this.nextStep;
			this.nextStep ="";
		}
		
		
	}
}
