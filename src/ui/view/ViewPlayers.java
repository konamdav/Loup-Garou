package ui.view;



import java.util.HashMap;
import java.util.Map.Entry;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ViewPlayers {
	private HashMap<String,ViewPlayer> viewPlayers;
	private SpriteBatch batch;

	public ViewPlayers(SpriteBatch batch) {
		this.viewPlayers = new HashMap<String,ViewPlayer>();
		this.batch=batch;
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
		ViewPlayer player = new ViewPlayer(x, y, status, dir, this.batch);
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
			if((int)(Math.random()*3) != 1){
				entry.getValue().setStatus("SLEEP");
			}
			else
			{
				entry.getValue().setStatus("WAKE");
			}
		}
	}

}
