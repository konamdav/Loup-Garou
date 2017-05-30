package sma.model;

import java.util.HashMap;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GameSettings {
	private HashMap<String, Integer> rolesSettings;
	private int nbHumans;

	public GameSettings() {
		super();

		this.nbHumans = 0;
		
		//Liste role par d�faut
		this.rolesSettings = new HashMap<String, Integer>();


		this.rolesSettings.put(Roles.WEREWOLF, 2);
		this.rolesSettings.put(Roles.CITIZEN, 0);
		this.rolesSettings.put(Roles.CUPID, 2);
		this.rolesSettings.put(Roles.LITTLE_GIRL, 0);
		this.rolesSettings.put(Roles.MEDIUM,1);
		this.rolesSettings.put(Roles.ANGEL,1);
		this.rolesSettings.put(Roles.FLUTE_PLAYER,1);
		this.rolesSettings.put(Roles.ANCIEN,1); //Si problème, demander à David il est chef de projet <3


	}
	
	public GameSettings(int werewolf, int citizen, int cupid, int little_girl, int medium) {
		super();
		//nb

		this.nbHumans = 0;
		
		this.rolesSettings = new HashMap<String, Integer>();
		this.rolesSettings.put(Roles.WEREWOLF, werewolf);
		this.rolesSettings.put(Roles.CITIZEN, citizen);
		this.rolesSettings.put(Roles.CUPID, cupid);
		this.rolesSettings.put(Roles.LITTLE_GIRL, little_girl);
		this.rolesSettings.put(Roles.MEDIUM, medium);

	}

	public int getNbHumans() {
		return nbHumans;
	}

	public void setNbHumans(int nbHumans) {
		this.nbHumans = Math.min(nbHumans, this.getPlayersCount());
	}

	@JsonIgnore
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

	@JsonIgnore
	public boolean isRoleRegistered(String key) {
		if(key.equals(Roles.CITIZEN))
		{
			return true;
		}
		else if(key.equals(Roles.WEREWOLF))
		{
			return rolesSettings.containsKey(key) && rolesSettings.get(key)>0
					|| rolesSettings.containsKey(Roles.GREAT_WEREWOLF) && rolesSettings.get(Roles.GREAT_WEREWOLF)>0
					|| rolesSettings.containsKey(Roles.WHITE_WEREWOLF) && rolesSettings.get(Roles.WHITE_WEREWOLF)>0;
		}
		return rolesSettings.containsKey(key) && rolesSettings.get(key)>0;
	}

	@JsonIgnore
	public int getPlayersCount()
	{
		int i = 0;
		for(Entry<String, Integer> entry : this.rolesSettings.entrySet())
		{
			i+= entry.getValue();
		}
		return i;
	}

	public void setRolesSettings(HashMap<String, Integer> rolesSettings) {
		this.rolesSettings = rolesSettings;
	}
}
