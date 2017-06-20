package sma.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveConfigSettings {
	private HashMap<String, Integer> config;

	public LiveConfigSettings(List<PlayerProfile> profiles) {
		super();
		config = new HashMap<String, Integer>();

		if(profiles!=null)
		{
			for(PlayerProfile p : profiles)
			{
				for(String role : p.getRoles())
				{
					if(!role.equals("HUMAN") && !role.equals("VICTIM"))
					{
						if(!config.containsKey(role))
						{
							config.put(role, 0);
						}

						if(!p.getStatus().equals(Status.DEAD))
						{
							config.put(role, config.get(role)+1);
						}
					}
				}

			}
		}
	}

	public HashMap<String, Integer> getConfig() {
		return config;
	}	

}
