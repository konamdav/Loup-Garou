package sma.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Serveur de partie
 * Conteneur syst�me de la plateforme
 * @author Davy
 *
 */
public class SystemContainer {

	public static String MAIN_PROPERTIES_FILE = "resources/sma/systemcontainer.properties";
	public static void main(String[] args)
	{
		new SystemContainer();
	}

	private AgentContainer mc = null;

	public SystemContainer()
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Runtime rt = Runtime.instance();
				Profile p = null;
				try{
					p = new ProfileImpl(MAIN_PROPERTIES_FILE);

					mc = rt.createMainContainer(p);	
					AgentController ac = mc.createNewAgent(
							"SYSTEM_CONTROLLER_AGENT", "sma.system_controller_agent.SystemControllerAgent", null);
					ac.start();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}
	
	public SystemContainer(final String name, final String ip, String port)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Runtime rt = Runtime.instance();
				Profile p = null;
				try{
					p = new ProfileImpl(MAIN_PROPERTIES_FILE);
					p.setParameter("platform-id", name);
					p.setParameter("host", ip);
					
					mc = rt.createMainContainer(p);	
					AgentController ac = mc.createNewAgent(
							"SYSTEM_CONTROLLER_AGENT", "sma.system_controller_agent.SystemControllerAgent", null);
					ac.start();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	public void stop()
	{
		if(mc!=null)
		{
			try {
				mc.kill();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
}
