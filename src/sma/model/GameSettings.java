package sma.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class GameSettings {
	private HashMap<String, Integer> rolesSettings;
	
	public GameSettings() {
		super();
		
		//Liste role par défaut
		this.rolesSettings = new HashMap<String, Integer>();
		this.rolesSettings.put(Roles.CITIZEN, 3);
		this.rolesSettings.put(Roles.WEREWOLF, 3);
		this.rolesSettings.put(Roles.CUPID, 0);
		this.rolesSettings.put(Roles.LITTLE_GIRL, 0);
		this.rolesSettings.put(Roles.MEDIUM, 0);
		
	}



	public HashMap<String, Integer> getCurrentRolesSettings() {
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		for(Entry<String, Integer> entry : this.rolesSettings.entrySet())
		{
			if(this.isRoleRegistered(entry.getKey()))
			{
				tmp.put(entry.getKey(), entry.getValue());
			}
		}
		return tmp;
	}
	
	public HashMap<String, Integer> getRolesSettings() {
		return rolesSettings;
	}
	
	public boolean isRoleRegistered(String key) {
		return rolesSettings.containsKey(key) && rolesSettings.get(key)>0;
	}
	
	public int getPlayersCount()
	{
		int i = 0;
		for(Entry<String, Integer> entry : this.rolesSettings.entrySet())
		{
			i+= entry.getValue();
		}
		return i;
	}
}
