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
import sma.model.Functions;
import sma.model.Roles;
import sma.model.Status;

/**
 * Behaviour qui prï¿½vient la fin de jeu
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
				this.sender = message.getSender();
				this.nextStep = STATE_SEND_ANSWER;
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
			String[] services11 = {Roles.WEREWOLF, Status.SLEEP};
			werewolves.addAll(DFServices.findGamePlayerAgent(services11, this.gameControllerAgent, this.gameControllerAgent.getGameid()));

			String[] services2 = {Roles.CITIZEN, Status.WAKE};
			List<AID> citizens = DFServices.findGamePlayerAgent(services2, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			String[] services22 = {Roles.CITIZEN, Status.SLEEP};
			citizens.addAll(DFServices.findGamePlayerAgent(services22, this.gameControllerAgent, this.gameControllerAgent.getGameid()));

			
			String[] services3 = {Roles.LOVER, Status.WAKE};
			List<AID> lovers = DFServices.findGamePlayerAgent(services3, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			String[] services33 = {Roles.LOVER, Status.SLEEP};
			lovers.addAll(DFServices.findGamePlayerAgent(services33, this.gameControllerAgent, this.gameControllerAgent.getGameid()));

			String[] services4 = {Roles.FLUTE_PLAYER, Status.WAKE};
			List<AID> flutePlayers = DFServices.findGamePlayerAgent(services4, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			String[] services44 = {Roles.FLUTE_PLAYER, Status.SLEEP};
			flutePlayers.addAll(DFServices.findGamePlayerAgent(services44, this.gameControllerAgent, this.gameControllerAgent.getGameid()));

			String[] services5 = {Roles.FLUTE_PLAYER, Status.WAKE};
			List<AID> charmed = DFServices.findGamePlayerAgent(services5, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			String[] services55 = {Roles.FLUTE_PLAYER, Status.SLEEP};
			charmed.addAll(DFServices.findGamePlayerAgent(services55, this.gameControllerAgent, this.gameControllerAgent.getGameid()));

			String[] services6 = {Roles.ANGEL, Status.DEAD};
			List<AID> angel = DFServices.findGamePlayerAgent(services6, this.gameControllerAgent, this.gameControllerAgent.getGameid());
			
			
			
			
			System.err.println("W Size = "+werewolves.size());
			System.err.println("C Size = "+citizens.size());
			System.err.println("L Size = "+lovers.size());
			System.err.println("FP + CH Size = "+(charmed.size()+flutePlayers.size()));
			
			System.err.println("DEAD Size = "+DFServices.findGamePlayerAgent("DEAD", this.gameControllerAgent, this.gameControllerAgent.getGameid()).size());
			System.err.println("SLEEP Size = "+DFServices.findGamePlayerAgent("SLEEP", this.gameControllerAgent, this.gameControllerAgent.getGameid()).size());
			System.err.println("MAYOR Size = "+DFServices.findGamePlayerAgent("MAYOR", this.gameControllerAgent, this.gameControllerAgent.getGameid()).size());
			
			/** plus de loups garous **/
			if( werewolves.isEmpty()) 
			{
				System.err.println("No werewolf");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
				
			}
			/** plus de de villeagois **/
			else if(werewolves.size() == citizens.size() && citizens.size()!=0) 
			{
				System.err.println("No simple citizen");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}
			/** egalité **/
			else if(0 == citizens.size()) 
			{
				System.err.println("equality - all people are dead");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}
			/** angel tué au premier tour **/
			else if(this.gameControllerAgent.getNum_turn() == 1 
					&& this.gameControllerAgent.getGameSettings().isRoleRegistered(Roles.ANGEL)
					&& !angel.isEmpty() ) 
			{
				System.err.println("an angel died during the first turn");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}
			/** le joueur de flute a charmé tout le monde **/
			else if(this.gameControllerAgent.getGameSettings().isRoleRegistered(Roles.FLUTE_PLAYER)
					&& (charmed.size()+flutePlayers.size() == citizens.size())) 
			{
				System.err.println("flute player won²");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}
			/** les amoureux ont survécus **/
			else if(lovers.size() == citizens.size()) 
			{
				System.err.println("just lovers");
				this.gameControllerAgent.setCheckEndGame(true);
				message.setConversationId("END_GAME");
				this.nextStep = STATE_NOTIFY_END_GAME;
			}	
			else
			{
				System.err.println("GAME CONTINUES");
				message.setConversationId("CONTINUE_GAME");
				this.nextStep = STATE_END;
			}
			
			this.gameControllerAgent.send(message);
		}
		else if(this.step.equals(STATE_NOTIFY_END_GAME))
		{
			//maj environment
			Functions.setEndGame(this.gameControllerAgent, this.gameControllerAgent.getGameid());
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
