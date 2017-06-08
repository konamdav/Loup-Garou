package sma.system_controller_agent;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.launch.GameContainer;
import sma.model.Functions;

public class RemoveGameBehaviour extends SimpleBehaviour{
	private SystemControllerAgent ctrlAgent ;

	public RemoveGameBehaviour(SystemControllerAgent agent) {
		super();
		this.ctrlAgent = agent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("REMOVE"));

		ACLMessage message = this.myAgent.receive(mt);
		if (message != null) 
		{
			
			int gameid = Integer.parseInt(message.getContent());
			System.err.println("RETRAIT DU CONTAINER "+gameid);

			GameContainer c = null;
		
			for(GameContainer cc : 	this.ctrlAgent.getContainers())
			{
				if(cc.getGameid()== gameid)
				{
					c = cc;
				}
			}
			
			if(c!=null){
				System.err.println("DESTRUCTION OK DU CONTAINER");

				this.ctrlAgent.getContainers().remove(c);
				
			}
		}
		else{
			block(1000);
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}
