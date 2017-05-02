package sma.citizen_controller_agent;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour tuant les joueurs désignés comme victimes
 * @author Davy
 *
 */
public class KillVictimsBehaviour extends SimpleBehaviour
{
	private CitizenControllerAgent citizenControllerAgent;
	private boolean flag;
	private final static String STATE_INIT = "INIT";
	private final static String STATE_SEND_GET_ROLE = "SEND_GET_ROLE";
	private final static String STATE_RECEIVE_GET_ROLE = "RECEIVE_GET_ROLE";
	private final static String STATE_INFORM_ALL_ROLE = "RECEIVE_INFORM_ALL_ROLE";
	private final static String STATE_SEND_KILL = "SEND_KILL";
	private final static String STATE_RECEIVE_KILL = "RECEIVE_KILL";
	private final static String STATE_END = "END";
	private String step;
	private String nextStep;

	private AID currentVictim;

	public KillVictimsBehaviour(CitizenControllerAgent citizenControllerAgent) {
		super();
		this.citizenControllerAgent = citizenControllerAgent;

		this.currentVictim = null;
		this.step = STATE_INIT;
		this.nextStep ="";
	}

	@Override
	public void action() {

		//System.out.println("STATE KILL VICTIMS "+this.step);


		if(this.step.equals(STATE_INIT))
		{
			this.currentVictim = null;

			/** pop la victime **/
			if(this.citizenControllerAgent.getVictims().isEmpty())
			{
				this.nextStep =STATE_END;
			}
			else
			{
				this.currentVictim = this.citizenControllerAgent.getVictims().pop();
				System.out.println("CURRENT VICTIM "+this.currentVictim.getName());

				this.nextStep =STATE_SEND_GET_ROLE;
			}
		}
		else if(this.step.equals(STATE_SEND_GET_ROLE))
		{
			this.nextStep = STATE_RECEIVE_GET_ROLE;
		}
		else if(this.step.equals(STATE_RECEIVE_GET_ROLE))
		{			
			this.nextStep =STATE_INFORM_ALL_ROLE;
		}
		else if(this.step.equals(STATE_INFORM_ALL_ROLE))
		{
			this.nextStep =STATE_SEND_KILL;
		}
		else if(this.step.equals(STATE_SEND_KILL))
		{
			System.err.println("SEND KILL "+currentVictim.getName());
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId("KILL_PLAYER");
			msg.setSender(this.citizenControllerAgent.getAID());
			msg.addReceiver(this.currentVictim);
			this.citizenControllerAgent.send(msg);
			
			this.nextStep = STATE_RECEIVE_KILL;
		}	
		else if(this.step.equals(STATE_RECEIVE_KILL))
		{
			if(this.currentVictim==null && this.citizenControllerAgent.getVictims().isEmpty())
			{
				System.out.println("PAS DE VICTIMES");
				this.nextStep = STATE_END;
			}
			else
			{
				//Message get from InitBehaviour game_control_Agent
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
						MessageTemplate.MatchConversationId("DEAD_PLAYER"));
					
				ACLMessage message = this.myAgent.receive(mt);
				if(message != null)
				{
					this.nextStep = STATE_INIT;
				}
				else
				{
					this.nextStep = STATE_RECEIVE_KILL;
					block();
				}
				
				
			}

		}
		else if(this.step.equals(STATE_END))
		{
			this.citizenControllerAgent.setFlag_victims(true);
			flag = true;
		}

		if(!this.nextStep.isEmpty())
		{
			this.step = this.nextStep;
			this.nextStep ="";
		}
	}

	@Override
	public boolean done() {
		return flag;
	}
}
