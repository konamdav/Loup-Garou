package ui.sma;

import java.util.UUID;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import sma.model.GameSettings;
import ui.view.App;

public class UIContainer {
	private String id = UUID.randomUUID().toString();
	public static String MAIN_PROPERTIES_FILE = "resources/sma/gamecontainer.properties";
	private AgentContainer container;
	
	public UIContainer(String ip)
	{		
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			
			p.setParameter("container-name", "ui_container_"+id);
			p.setParameter("host", ip);
			container = rt.createAgentContainer(p);	
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public UIContainer(String ip, App app)
	{		
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			
			p.setParameter("container-name", "ui_container_"+id);
			p.setParameter("host", ip);
			p.setParameter("port", "1099");
			container = rt.createAgentContainer(p);	
			
			AgentController ac = container.createNewAgent("UI_AGENT_"+id, "ui.agent.uiAgent", new Object[]{app});
			ac.start();
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public AgentContainer getContainer() {
		return container;
	}
	

}
