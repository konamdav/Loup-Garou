package sma.environment_agent;

import java.io.IOException;
import java.util.List;

import sma.model.DFServices;
import sma.model.GameInformations;

import org.codehaus.jackson.map.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendBehaviour extends OneShotBehaviour {

	private EnvironmentAgent envAgent;
	private ACLMessage message;


	public SendBehaviour(EnvironmentAgent envAgent, ACLMessage message) {
		super();
		this.envAgent = envAgent;
		this.message = message;
	}


	@Override
	public void action() {
		//System.out.println("SEND DATA");
		boolean send = true;
		ACLMessage reply = message.createReply();
		ObjectMapper mapper = new ObjectMapper();
		String contentString = "";
		if(message.getConversationId().equals("GLOBAL_VOTE_RESULTS"))
		{
			try {
				contentString = mapper.writeValueAsString(this.envAgent.getGlobalResults());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("DAY_STATE"))
		{
			contentString = this.envAgent.getDayState();	
		}
		else if(message.getConversationId().equals("TURN"))
		{
			contentString = this.envAgent.getTurn();	
		}
		else if(message.getConversationId().equals("END_GAME"))
		{
			contentString = this.envAgent.isEndGame() ? "true" : "false" ;	
		}
		else if(message.getConversationId().equals("NUM_TURN"))
		{
			contentString = ""+this.envAgent.getNum_turn();	
		}
		else if(message.getConversationId().equals("ACTION_LOGS"))
		{
			try {
				contentString = mapper.writeValueAsString(this.envAgent.getActionLogs());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(message.getConversationId().equals("GAME_INFORMATIONS" + envAgent.getGameid()))
		{
			//System.out.println("SEND GAMEINFORMATIONS");
			if(!this.envAgent.isGame_mode()){

				GameInformations gi = new GameInformations();
				gi.setActionLogs(envAgent.getActionLogs());
				gi.setCurrentResults(envAgent.getCurrentResults());
				gi.setDayState(envAgent.getDayState());
				gi.setEndGame(envAgent.isEndGame());
				gi.setProfiles(DFServices.getPlayerProfiles(this.envAgent.isGame_mode(), this.envAgent.getCptHuman(), "", envAgent, envAgent.getGameid()));
				gi.setTurn(envAgent.getTurn());
				gi.setNum_turn(envAgent.getNum_turn());
				gi.setVote(envAgent.getHumanVoteRequest());

				try {
					contentString = mapper.writeValueAsString(gi);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				//init de la map ?
				if(!this.envAgent.getAffectationGameUI().containsKey(message.getSender()))
				{
					List<AID> humans = DFServices.findGamePlayerAgent("HUMAN", this.envAgent, this.envAgent.getGameid());
					boolean flag = false;
					int cpt = 0;
					while (cpt < humans.size() && !flag)
					{
						if(!this.envAgent.getAffectationGameUI().containsValue(humans.get(cpt)))
						{
							flag = true;
							send = false;
							//affectation du joueur a l'ui
							this.envAgent.getAffectationGameUI().put(message.getSender(), humans.get(cpt));

							//redirection
							ACLMessage redirection = new ACLMessage(ACLMessage.REQUEST);
							redirection.setConversationId("GAME_INFORMATIONS" + envAgent.getGameid());
							redirection.setSender(message.getSender());

							//System.err.println("AFFECTATION UI => "+"ENVIRONMENT_"+humans.get(cpt).getLocalName());
							
							List<AID> agents = DFServices.findGameControllerAgent("ENVIRONMENT_"+humans.get(cpt).getLocalName(), this.envAgent, this.envAgent.getGameid());
							if(!agents.isEmpty()){
								redirection.addReceiver(agents.get(0));
								this.envAgent.send(redirection);
								
								//System.err.println("SEND");
							}
						}

						++cpt;
					}

					//plus de joueur a affecter
					if(!flag)
					{
						this.envAgent.getAffectationGameUI().put(message.getSender(), null);

						GameInformations gi = new GameInformations();
						gi.setActionLogs(envAgent.getActionLogs());
						gi.setCurrentResults(envAgent.getCurrentResults());
						gi.setDayState(envAgent.getDayState());
						gi.setEndGame(envAgent.isEndGame());
						gi.setProfiles(DFServices.getPlayerProfiles(false, this.envAgent.getCptHuman(), "", envAgent, envAgent.getGameid()));
						gi.setTurn(envAgent.getTurn());
						gi.setNum_turn(envAgent.getNum_turn());
						gi.setVote(envAgent.getHumanVoteRequest());

						try {
							contentString = mapper.writeValueAsString(gi);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				else if(this.envAgent.getAffectationGameUI().get(message.getSender())==null)
				{
					//System.err.println("SEND PAR DEFAUT "+this.envAgent.getAffectationGameUI().get(message.getSender()));
					GameInformations gi = new GameInformations();
					gi.setActionLogs(envAgent.getActionLogs());
					gi.setCurrentResults(envAgent.getCurrentResults());
					gi.setDayState(envAgent.getDayState());
					gi.setEndGame(envAgent.isEndGame());
					gi.setProfiles(DFServices.getPlayerProfiles(false, this.envAgent.getCptHuman(), "", envAgent, envAgent.getGameid()));
					gi.setTurn(envAgent.getTurn());
					gi.setNum_turn(envAgent.getNum_turn());
					gi.setVote(envAgent.getHumanVoteRequest());

					try {
						contentString = mapper.writeValueAsString(gi);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					send = false;
				}
			}
		}

		if(send == true){
			reply.setContent(contentString);
			this.envAgent.send(reply);
		}
	}



}
