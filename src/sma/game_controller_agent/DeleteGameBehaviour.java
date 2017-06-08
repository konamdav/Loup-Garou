package sma.game_controller_agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import sma.model.DFServices;

public class DeleteGameBehaviour extends WakerBehaviour{
	GameControllerAgent gameAgent;
	public DeleteGameBehaviour(GameControllerAgent a) {

		super(a, 5000);
		gameAgent = a;
	}

	@Override
	protected void onWake() {

		System.err.println("DESTRUCTION DU CONTAINER");

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setConversationId("REMOVE");
		msg.setSender(this.myAgent.getAID());
		msg.setContent(""+this.gameAgent.getGameid());
		AID agent = DFServices.getSystemController(this.myAgent);
		msg.addReceiver(agent);
		this.myAgent.send(msg);

		try {
			this.myAgent.getContainerController().kill();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
