package sma.generic_agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.model.ScoreResults;
import sma.model.VoteRequest;
import sma.model.VoteResults;
import sma.player_agent.PlayerAgent;

public class AbstractDeathBehaviour extends SimpleBehaviour{
	private PlayerAgent agent;

	//TODO Do deathBehaviour

	public AbstractDeathBehaviour(PlayerAgent agent) {
		super();
		this.agent = agent;

	}

	@Override
	public void action() {
		//Do first behaviour common to all behaviour, which laucnh all specific bevaiour of this agent. 
		//Villageois turn
		
	}

	@Override
	public boolean done() {
		return false;
	}

}
