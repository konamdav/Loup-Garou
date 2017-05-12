package sma.generic_init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.citizen_agent.CitizenInitBehaviour;
import sma.generic_agent.MayorInitBehaviour;
import sma.generic_vote.AbstractVoteBehaviour;
import sma.lover_behaviour.LoverInitBehaviour;
import sma.model.DFServices;
import sma.model.Roles;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;
import sma.werewolf_agent.WerewolfInitBehaviour;

public class FactoryInitBehaviour extends CyclicBehaviour{
	private PlayerAgent agent;

	//TODO Do deathBehaviour

	public FactoryInitBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;

	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("INIT_ROLE"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			String role_receive = message.getContent();
			//System.out.println("FACTORY INIT BEHAVIOUR "+role_receive+ " TO THIS PLAYER "+this.agent.getName());				
			//TODO TODO TODO IMPORTANT CHANGE COMPILANCE TRUC VERSTION TO 1.7 FOR THIS SWITCH STRING
			switch (role_receive) {
			case Roles.CITIZEN:
				this.agent.addBehaviour(new CitizenInitBehaviour(this.agent));
				break;
			case Roles.WEREWOLF:
				this.agent.addBehaviour(new WerewolfInitBehaviour(this.agent));
				break;
			case Roles.LOVER:
				this.agent.addBehaviour(new LoverInitBehaviour(this.agent));
				break;
			case Roles.MAYOR:
				this.agent.addBehaviour(new MayorInitBehaviour(this.agent));
				break;
			default:
				System.err.print("Erreur role not valid" );
				//GET OUT, not send message
				break;
			}
					//Envoie le rôle au controlleur.
			
			//TODO LOOk if put this in the genericinitbehviour
			ACLMessage messageRequest = new ACLMessage(ACLMessage.INFORM);
			messageRequest.setSender(this.agent.getAID());
			
			List<AID> agents = DFServices.findGameControllerAgent("GAME", this.myAgent, this.agent.getGameid());
			if(!agents.isEmpty())
			{
				messageRequest.addReceiver(agents.get(0));
				messageRequest.setConversationId("ATTRIBUTION_ROLE");
				this.myAgent.send(messageRequest);	
			}

		}
		else
		{
			block();
		}
	}


}
