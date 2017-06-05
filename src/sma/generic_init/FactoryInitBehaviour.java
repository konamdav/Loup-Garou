package sma.generic_init;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.citizen_agent.AngelInitBehaviour;
import sma.citizen_agent.CitizenInitBehaviour;
import sma.cupid.CupidInitBehaviour;
import sma.flute_player.CharmedInitBehaviour;
import sma.flute_player.FlutePlayerInitBehaviour;
import sma.littlegirl.LittleGirlInitBehaviour;

import sma.generic_vote.AbstractVoteBehaviour;
import sma.hunter.HunterInitBehaviour;
import sma.lover_behaviour.LoverInitBehaviour;
import sma.medium_behaviour.MediumInitBehaviour;
import sma.model.Roles;
import sma.player_agent.MayorInitBehaviour;
import sma.player_agent.PlayerAgent;
import sma.scapegoat.ScapegoatInitBehaviour;
import sma.werewolf_agent.WerewolfInitBehaviour;

public class FactoryInitBehaviour extends CyclicBehaviour{
	private PlayerAgent agent;

	private String step;
	private String nextStep;

	private AID sender;
	
	private final String STATE_WAIT_ROLE ="STATE_WAIT_ROLE";
	private final String STATE_ANSWER_INIT_ROLE ="STATE_ANSWER_INIT_ROLE";
	
	public FactoryInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;
		this.nextStep = "";
		this.step = STATE_WAIT_ROLE;
		this.sender = null;
	}

	
	//TODO MAchine a état, 
	//1er => Attente init_role
	//2eme => Attente accord 

	@Override
	public void action() {
		
		if(step.equals(STATE_WAIT_ROLE))
		{
			//Message get from NewMainRoleBehaviour game_control_Agent

			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("INIT_ROLE"));

			ACLMessage message = this.myAgent.receive(mt);
			if (message != null) 
			{
				String role_receive = message.getContent();
				this.sender = message.getSender();

				System.out.println("FACTORY INIT BEHAVIOUR "+role_receive+ " TO THIS PLAYER "+this.agent.getName());
				
				switch (role_receive) {
				case Roles.CITIZEN:
					this.agent.addBehaviour(new CitizenInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.WEREWOLF:
					this.agent.addBehaviour(new WerewolfInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.LOVER:
					this.agent.addBehaviour(new LoverInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.MAYOR:
					this.agent.addBehaviour(new MayorInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.MEDIUM:
					this.agent.addBehaviour(new MediumInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.FLUTE_PLAYER:
					this.agent.addBehaviour(new FlutePlayerInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.CHARMED:
					this.agent.addBehaviour(new CharmedInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.SCAPEGOAT:
					this.agent.addBehaviour(new ScapegoatInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.ANGEL:
					this.agent.addBehaviour(new AngelInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.CUPID:
					this.agent.addBehaviour(new CupidInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.LITTLE_GIRL:
					this.agent.addBehaviour(new LittleGirlInitBehaviour(this.agent, this.agent.getAID()));
					break;
				case Roles.HUNTER:
					this.agent.addBehaviour(new HunterInitBehaviour(this.agent, this.agent.getAID()));
				default:
					System.err.print("Erreur role not valid" );
						//throw new Exception();
					//GET OUT, not send message
					break;
				}
				this.nextStep = STATE_ANSWER_INIT_ROLE;
			}
			else
			{
				this.nextStep = "";
				block();
			}
		}
		else if(step.equals(STATE_ANSWER_INIT_ROLE))
		{
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.AGREE),
					MessageTemplate.MatchConversationId("INIT_ROLE"));
			
			//TODO DO fail answer

			System.out.println("Reply to controler init done ");

			ACLMessage message = this.myAgent.receive(mt);
			if(message != null)
			{
				//TODO Machine à état too 
				ACLMessage messageRequest = new ACLMessage(ACLMessage.INFORM);
				messageRequest.setSender(this.agent.getAID());
				messageRequest.setConversationId("INIT_ROLE");
				messageRequest.addReceiver(this.sender);
				this.myAgent.send(messageRequest);	
				
				this.nextStep = STATE_WAIT_ROLE; //Return to begin 
			}
			else
			{
				this.nextStep = "";
				block();
			}

		}
	if(!this.nextStep.isEmpty())
	{
		this.step = this.nextStep;
		this.nextStep ="";
	}

	}


}

//TODO Do deathBehaviour
