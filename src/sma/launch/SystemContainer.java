package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class SystemContainer {

	public static String MAIN_PROPERTIES_FILE = "resources/sma/systemcontainer.properties";
	public static void main(String[] args)
	{
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			AgentController ac = mc.createNewAgent(
					"SYSTEM_CONTROLLER_AGENT", "sma.system_controller_agent.SystemControllerAgent", null);
			ac.start();
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public SystemContainer()
	{
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			rt.createMainContainer(p);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
}
