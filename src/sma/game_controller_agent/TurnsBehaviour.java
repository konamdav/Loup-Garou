package sma.game_controller_agent;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.DFServices;
import sma.model.Functions;
import sma.model.Roles;
import sma.model.Status;


/***
 * Coordonne les tours
 * @author Davy
 *
 */
public class TurnsBehaviour extends SimpleBehaviour 
{
	private GameControllerAgent controllerAgent;
	
	private final String STATE_INIT = "INIT";
	private final String STATE_WAITING = "WAITING";
	private final String STATE_START_CITIZEN_TURN = "START_CITIZEN_TURN";
	private final String STATE_STOP_CITIZEN_TURN = "STOP_CITIZEN_TURN";

	private final String STATE_PREINIT = "STATE_PREINIT";
	private final String STATE_CHECK_ENDGAME_REQUEST = "STATE_CHECK_ENDGAME_REQUEST";
	private final String STATE_CHECK_ENDGAME_RECEIVE = "STATE_CHECK_ENDGAME_RECEIVE";

	private final String STATE_START_VOLEUR_TURN = "START_VOLEUR_TURN";
	private final String STATE_STOP_VOLEUR_TURN = "STOP_VOLEUR_TURN";

	private final String STATE_START_WEREWOLF_TURN = "START_WEREWOLF_TURN";
	private final String STATE_STOP_WEREWOLF_TURN = "STOP_WEREWOLF_TURN";

	private final String STATE_START_GREAT_WEREWOLF_TURN = "START_GREAT_WEREWOLF_TURN";
	private final String STATE_STOP_GREAT_WEREWOLF_TURN = "STOP_GREAT_WEREWOLF_TURN";

	private final String STATE_START_WHITE_WEREWOLF_TURN = "START_WHITE_WEREWOLF_TURN";
	private final String STATE_STOP_WHITE_WEREWOLF_TURN = "STOP_WHITE_WEREWOLF_TURN";
	
	private final String STATE_START_FLUTE_PLAYER_TURN = "START_FLUTE_PLAYER_TURN";
	private final String STATE_STOP_FLUTE_PLAYER_TURN = "STOP_FLUTE_PLAYER_TURN";

	private final String STATE_START_CUPID_TURN = "START_CUPID_TURN";
	private final String STATE_STOP_CUPID_TURN = "STOP_CUPID_TURN";

	private final String STATE_START_MEDIUM_TURN = "START_MEDIUM_TURN";
	private final String STATE_STOP_MEDIUM_TURN = "STOP_MEDIUM_TURN";

	private final String STATE_START_FAMILY_TURN = "START_FAMILY_TURN";
	private final String STATE_STOP_FAMILY_TURN = "STOP_FAMILY_TURN";

	private final String STATE_START_EXORCIST_TURN = "START_EXORCIST_TURN";
	private final String STATE_STOP_EXORCIST_TURN = "STOP_EXORCIST_TURN";

	private final String STATE_START_WITCH_TURN = "START_WITCH_TURN";
	private final String STATE_STOP_WITCH_TURN = "STOP_WITCH_TURN";
	
	private final String STATE_START_SALVATOR_TURN = "START_SALVATOR_TURN";
	private final String STATE_STOP_SALVATOR_TURN = "STOP_SALVATOR_TURN";

	private final String STATE_END = "END";

	private  final String STATE_POSTEND = "POSTEND";
	private boolean flag_done;

	private boolean flag_cupid; 
	private boolean flag_voleur; 
	private boolean flag_white_werewolf; //1 turn on two 

	private String step;
	private String nextStep;

	public TurnsBehaviour(GameControllerAgent controllerAgent) {
		super();

		this.step = STATE_PREINIT;
		this.nextStep = "";
		this.flag_cupid = false;
		this.flag_voleur = false;
		this.flag_done = false;
		this.flag_white_werewolf = true; //Start by allowed this turn
 
		this.controllerAgent = controllerAgent;	
	}

	@Override
	public void action() {

		//System.out.println("STATE GC = "+this.step);

		if(this.step.equals(STATE_PREINIT))
		{	
			this.nextStep = STATE_CHECK_ENDGAME_REQUEST;
		}
		else if(this.step.equals(STATE_CHECK_ENDGAME_REQUEST))
		{	
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setSender(this.myAgent.getAID());
			message.addReceiver(this.myAgent.getAID());
			message.setConversationId("CHECK_END_GAME");
			this.myAgent.send(message);

			this.nextStep = STATE_CHECK_ENDGAME_RECEIVE;
		}
		else if(this.step.equals(STATE_CHECK_ENDGAME_RECEIVE))
		{	
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("CONTINUE_GAME"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				System.err.println("RECEIVE CONTINUE GAME");
				this.nextStep = STATE_INIT;
			}
			else
			{
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("END_GAME"));

				message = this.myAgent.receive(mt);
				if(message!=null)
				{
					this.nextStep = STATE_POSTEND;
				}
				else
				{
					this.nextStep = STATE_CHECK_ENDGAME_RECEIVE;
					block(1000);
				}
			}
		}
		else if(this.step.equals(STATE_INIT))
		{	

			System.gc();

			if(this.controllerAgent.isCheckEndGame())
			{
				this.nextStep = STATE_POSTEND;
			}
			else
			{
				this.controllerAgent.incTurn();
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
				System.err.println("DAY "+this.controllerAgent.getNum_turn());
				Functions.sendNumTurn(this.controllerAgent.getNum_turn(), this.controllerAgent, this.controllerAgent.getGameid());
				//this.nextStep = STATE_START_CUPID_TURN;
				this.nextStep = STATE_START_VOLEUR_TURN;

			}
		}
		else if (this.step.equals(STATE_START_VOLEUR_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent(Roles.THIEF, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.THIEF, Status.SLEEP};
				List<AID> cupids = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = cupids.size();

				if(nbPlayers > 0 && !this.flag_voleur)
				{
					Functions.updateTurn(Roles.THIEF, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des voleurs", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.flag_voleur = true;
					this.nextStep = STATE_STOP_VOLEUR_TURN;
				}
				else
				{
					this.nextStep = STATE_START_CUPID_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_CUPID_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_VOLEUR_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_CUPID_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_VOLEUR_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_START_CUPID_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent(Roles.CUPID, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.CUPID, Status.SLEEP};
				List<AID> cupids = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = cupids.size();

				if(nbPlayers > 0 && !this.flag_cupid)
				{
					Functions.updateTurn(Roles.CUPID, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des cupidons", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.flag_cupid = true;
					this.nextStep = STATE_STOP_CUPID_TURN;
				}
				else
				{
					this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
			}


		}
		else if (this.step.equals(STATE_STOP_CUPID_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_FLUTE_PLAYER_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_CUPID_TURN;
				block(1000);
			}

		}

		else if (this.step.equals(STATE_START_FLUTE_PLAYER_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent(Roles.FLUTE_PLAYER, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.FLUTE_PLAYER, Status.SLEEP};
				List<AID> mediums = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = mediums.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.FLUTE_PLAYER, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des joueurs de flute", this.getAgent(), this.controllerAgent.getGameid());


					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_FLUTE_PLAYER_TURN;
				}
				else
				{
					this.nextStep = STATE_START_MEDIUM_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_MEDIUM_TURN;
			}


		}
		else if (this.step.equals(STATE_STOP_FLUTE_PLAYER_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_MEDIUM_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_FLUTE_PLAYER_TURN;
				block(1000);
			}

		}

		else if (this.step.equals(STATE_START_MEDIUM_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent(Roles.MEDIUM, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.MEDIUM, Status.SLEEP};
				List<AID> mediums = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = mediums.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.MEDIUM, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des mediums", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_MEDIUM_TURN;
				}
				else
				{
					this.nextStep = STATE_START_FAMILY_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_FAMILY_TURN;
			}

		}
		else if (this.step.equals(STATE_STOP_MEDIUM_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_FAMILY_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_MEDIUM_TURN;
				block(1000);
			}

		}
		else if (this.step.equals(STATE_START_FAMILY_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent(Roles.FAMILY, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{		
				String [] args = {Roles.FAMILY, Status.SLEEP};
				List<AID> family = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = family.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.FAMILY, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour de la famille", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_FAMILY_TURN;
				}
				else
				{
					this.nextStep = STATE_START_EXORCIST_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_EXORCIST_TURN;
			}

		}
		else if (this.step.equals(STATE_STOP_FAMILY_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_EXORCIST_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_FAMILY_TURN;
				block(1000);
			}

		}
		else if (this.step.equals(STATE_START_EXORCIST_TURN))
		{
			System.err.println("Check exorcists");
			List<AID> agents = DFServices.findGameControllerAgent(Roles.EXORCIST, this.myAgent, this.controllerAgent.getGameid());
			System.err.println("Check exorcists " + agents);

			if(!agents.isEmpty())
			{		
				String [] args = {Roles.EXORCIST, Status.SLEEP};
				System.err.println("Tour des exorcists");
				
				List<AID> family = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = family.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.EXORCIST, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des exorcists", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_EXORCIST_TURN;
				}
				else
				{
					this.nextStep = STATE_START_WEREWOLF_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_WEREWOLF_TURN;
			}

		}
		else if (this.step.equals(STATE_STOP_EXORCIST_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_WEREWOLF_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_EXORCIST_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_START_WEREWOLF_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent("WEREWOLF", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{			
				Functions.updateTurn(Roles.WEREWOLF, controllerAgent, controllerAgent.getGameid());
				Functions.newActionImportantToLog("Tour des loups", this.getAgent(), this.controllerAgent.getGameid());

				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("START_TURN");
				message.setSender(this.getAgent().getAID());
				message.addReceiver(agents.get(0));
				this.getAgent().send(message);

				this.nextStep = STATE_STOP_WEREWOLF_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_WEREWOLF_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_GREAT_WEREWOLF_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_WEREWOLF_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_START_GREAT_WEREWOLF_TURN))
		{

			List<AID> agents = DFServices.findGameControllerAgent(Roles.GREAT_WEREWOLF, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{	
				String [] args = {Roles.GREAT_WEREWOLF, Status.SLEEP};
				List<AID> werewolves = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = werewolves.size();
				
				String [] args2 = {Roles.WEREWOLF, Status.DEAD};
				List<AID> werewolvesDead = DFServices.findGamePlayerAgent(args2, this.controllerAgent, this.controllerAgent.getGameid()); //David encore une erreur --'				
				int nbPlayersDead = werewolvesDead.size();

				if(nbPlayersDead == 0 && nbPlayers > 0  )
				{
					Functions.updateTurn(Roles.GREAT_WEREWOLF, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour du m�chant loup", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_GREAT_WEREWOLF_TURN;
				}
				else
				{
					this.nextStep = STATE_START_WHITE_WEREWOLF_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_WHITE_WEREWOLF_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_GREAT_WEREWOLF_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_WHITE_WEREWOLF_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_GREAT_WEREWOLF_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_START_WHITE_WEREWOLF_TURN))
		{
			System.err.println("Debut du " + STATE_START_WHITE_WEREWOLF_TURN + " flag  " +flag_white_werewolf );
			if (this.flag_white_werewolf == true){
				//Lance tour, et le fera pas au prochain tour
				this.flag_white_werewolf = false;
				List<AID> agents = DFServices.findGameControllerAgent(Roles.WHITE_WEREWOLF, this.myAgent, this.controllerAgent.getGameid());
				if(!agents.isEmpty())
				{	
					String [] args = {Roles.WHITE_WEREWOLF, Status.SLEEP};
					List<AID> whitewerewolves = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
					int nbWhiteWerewolves = whitewerewolves.size();
					
					String [] args2 = {Roles.WEREWOLF, Status.SLEEP};
					List<AID> werewolves = DFServices.findGamePlayerAgent(args2, this.controllerAgent, this.controllerAgent.getGameid());				
					int nbWerewolves = werewolves.size();


					if( nbWhiteWerewolves > 0  && nbWerewolves > nbWhiteWerewolves)
					{
						Functions.updateTurn(Roles.WHITE_WEREWOLF, controllerAgent, controllerAgent.getGameid());
						Functions.newActionImportantToLog("Tour des loups blancs", this.getAgent(), this.controllerAgent.getGameid());

						ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
						message.setConversationId("START_TURN");
						message.setSender(this.getAgent().getAID());
						message.addReceiver(agents.get(0));
						this.getAgent().send(message);

						this.nextStep = STATE_STOP_WHITE_WEREWOLF_TURN;
					}
					else
					{
						this.nextStep = STATE_START_WITCH_TURN;
					}
				}
				else
				{
					this.nextStep = STATE_START_WITCH_TURN;
				}
			}
			else {
				//Ne lance pas tour, et le fera au prochain tour
				this.flag_white_werewolf = true;
				this.nextStep = STATE_START_WITCH_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_WHITE_WEREWOLF_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_WITCH_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_WHITE_WEREWOLF_TURN;
				block(1000);
			}
		}

		else if (this.step.equals(STATE_START_WITCH_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent("WITCH", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{	
				String [] args = {Roles.WITCH, Status.SLEEP};
				List<AID> witches = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = witches.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.WITCH, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des sorci�res", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_WITCH_TURN;
				}
				else
				{
					this.nextStep = STATE_START_SALVATOR_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_SALVATOR_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_WITCH_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_SALVATOR_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_WITCH_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_START_CITIZEN_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent("CITIZEN", this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{				
				Functions.updateTurn(Roles.CITIZEN, controllerAgent, controllerAgent.getGameid());
				Functions.newActionImportantToLog("Tour des villageois", this.getAgent(), this.controllerAgent.getGameid());


				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId("START_TURN");
				message.setSender(this.getAgent().getAID());
				message.addReceiver(agents.get(0));
				this.getAgent().send(message);

				this.nextStep = STATE_STOP_CITIZEN_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_CITIZEN_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_END;
			}
			else
			{
				this.nextStep = STATE_STOP_CITIZEN_TURN;
				block(1000);
			}

		}
		else if (this.step.equals(STATE_START_SALVATOR_TURN))
		{
			List<AID> agents = DFServices.findGameControllerAgent(Roles.SALVATOR, this.myAgent, this.controllerAgent.getGameid());
			if(!agents.isEmpty())
			{	
				String [] args = {Roles.SALVATOR, Status.SLEEP};
				List<AID> salvators = DFServices.findGamePlayerAgent(args, this.controllerAgent, this.controllerAgent.getGameid());				
				int nbPlayers = salvators.size();

				if(nbPlayers > 0)
				{
					Functions.updateTurn(Roles.SALVATOR, controllerAgent, controllerAgent.getGameid());
					Functions.newActionImportantToLog("Tour des salvateurs", this.getAgent(), this.controllerAgent.getGameid());

					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId("START_TURN");
					message.setSender(this.getAgent().getAID());
					message.addReceiver(agents.get(0));
					this.getAgent().send(message);

					this.nextStep = STATE_STOP_SALVATOR_TURN;
				}
				else
				{
					this.nextStep = STATE_START_CITIZEN_TURN;
				}
			}
			else
			{
				this.nextStep = STATE_START_CITIZEN_TURN;
			}
		}
		else if (this.step.equals(STATE_STOP_SALVATOR_TURN))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("END_TURN"));

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				this.controllerAgent.doWait(1000);
				this.nextStep = STATE_START_CITIZEN_TURN;
			}
			else
			{
				this.nextStep = STATE_STOP_SALVATOR_TURN;
				block(1000);
			}
		}
		else if (this.step.equals(STATE_END))
		{
			ObjectMapper mapper = new ObjectMapper();
			DFServices.printProfiles(this.myAgent, this.controllerAgent.getGameid());

			this.nextStep = STATE_INIT;
		}
		else if (this.step.equals(STATE_POSTEND))
		{
			Functions.updateTurn("", controllerAgent, controllerAgent.getGameid());
			System.err.println("FINISH GAME");
			this.flag_done = true;
			
			this.controllerAgent.addBehaviour(new DeleteGameBehaviour(this.controllerAgent));
			
		}

		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return this.flag_done;
	}
}
