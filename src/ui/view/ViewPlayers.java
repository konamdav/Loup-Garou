package ui.view;



import java.util.HashMap;
import java.util.Map.Entry;

import javafx.scene.Group;


public class ViewPlayers {
	private HashMap<String,ViewPlayer> viewPlayers;
	private Group group;

	public ViewPlayers(Group group) {
		this.viewPlayers = new HashMap<String,ViewPlayer>();
		this.group=group;
	}


	public void drawPlayersSleep()
	{
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			if(entry.getValue().getStatus().equals("SLEEP"))
			{
				entry.getValue().update();
			}
		}
	}

	public void drawPlayersWake()
	{
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			if(entry.getValue().getStatus().equals("WAKE"))
			{
				entry.getValue().update();
			}
		}
	}

	public void drawPlayersDead()
	{
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			if(entry.getValue().getStatus().equals("DEAD"))
			{
				entry.getValue().update();
			}
		}
	}

	public ViewPlayer newPlayer(String name, String status, String dir, int x, int y)
	{
		ViewPlayer player = new ViewPlayer(x, y, status, dir, this.group);
		this.viewPlayers.put(name, player);
		return player;
	}


	public void wake()
	{
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			entry.getValue().setStatus("WAKE");
		}
	}

	public void sleep()
	{
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{

			entry.getValue().setStatus("SLEEP");
			entry.getValue().updateSleep();
		}
	}

}
