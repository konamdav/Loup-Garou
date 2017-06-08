package ui.model;



import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import sma.model.PlayerProfile;

public class ViewPlayers {
	private HashMap<String,ViewPlayer> viewPlayers;
	private SpriteBatch batch;
	private boolean init;

	public ViewPlayers(SpriteBatch batch) {
		this.viewPlayers = new HashMap<String,ViewPlayer>();
		this.batch=batch;
		this.init = false;
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


	public void dead() {

		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			if((int)(Math.random()*5) == 1){
				entry.getValue().setStatus("DEAD");
			}
		}

	}


	public String getLabel(int mapx, int mapy) {
		String label ="";
		for(Entry<String, ViewPlayer> entry : this.viewPlayers.entrySet())
		{
			if(mapx > entry.getValue().getPosition()[0] && mapx< entry.getValue().getPosition()[0]+64
					&& mapy > entry.getValue().getPosition()[1] && mapy < entry.getValue().getPosition()[1]+64)
			{
				label = entry.getKey();
				label+=entry.getValue().getRoles().getAllRolesLabel();
			}
		}
		return label;
	}

	public void updatePlayers(List<PlayerProfile> profiles)
	{
		if(profiles.isEmpty())
		{
			System.exit(1);
		}
		if(!this.init)
		{
			int nb = profiles.size();
			int cpt = 0;

			int n_rows = 0;
			int n_cols = 0 ;

			n_rows  = nb % 4;
			n_cols = nb / 4;

			//System.out.println("NC "+n_cols+" NR "+n_rows);

			if(n_cols+n_rows >= 2)
			{
				if(nb>4)
				{
					n_cols = (nb-4)/2 + (nb-4)%2;
					//n_cols+= n_cols-2+n_rows;
					n_rows = 2;
				}
			}
			else
			{
				n_rows = n_cols;
			}

			//System.out.println("NC "+n_cols+" NR "+n_rows);

			int ind_x = 6;
			int ind_y = 4;
			ViewPlayer player;
			for(int i = 0; i<n_rows; ++i)
			{
				if(cpt < profiles.size()){
					player = newPlayer(profiles.get(cpt).getName(),profiles.get(cpt).getStatus(), "RIGHT", ind_x-n_cols/2, ind_y-(n_rows-1-i) );
					++cpt;
				}
			}

			if(nb >= 2) {
				for(int i = 0; i<n_cols; ++i)
				{
					if(cpt < profiles.size()){
						player = newPlayer(profiles.get(cpt).getName(),profiles.get(cpt).getStatus(), "DOWN", ind_x+1+i-n_cols/2, ind_y+1);
						++cpt;
					}

				}

				if(nb >= 3){

					int ret = 0;
					if(n_cols%2!=0)
					{
						ret = 1;
					}

					for(int i = 0; i<n_rows; ++i)
					{
						if(cpt < profiles.size()){
							player = newPlayer(profiles.get(cpt).getName(),profiles.get(cpt).getStatus(), "LEFT", ind_x+1+ret+n_cols/2, ind_y-i );
							++cpt;
						}
					}

					if(nb >= 4){
						int reste = nb - (n_cols+2*n_rows);
						for(int i = 0; i<reste; ++i)
						{
							if(cpt < profiles.size()){
								player = newPlayer(profiles.get(cpt).getName(),profiles.get(cpt).getStatus(), "UP", ind_x+1+(n_cols-1-i)-n_cols/2, ind_y-1-n_rows/2);
								++cpt;
							}

						}
					}
				}
			}

			for(PlayerProfile p : profiles)
			{
				for(String role : p.getRoles())
				{
					if(this.viewPlayers.get(p.getName())!=null)
					{
						this.viewPlayers.get(p.getName()).getRoles().addNewRole(role);
					}
				}
			}

			this.init = true;

		}
		else
		{
			for(PlayerProfile p : profiles)
			{
				ViewPlayer viewPlayer = this.viewPlayers.get(p.getName());
				if(p.getStatus() != null & viewPlayer != null){
					viewPlayer.setStatus(p.getStatus());
					List<String> alreadyRoles = this.viewPlayers.get(p.getName()).getRoles().getListRole();

					for(String role : p.getRoles())
					{
						if(!alreadyRoles.contains(role)){
							this.viewPlayers.get(p.getName()).getRoles().addNewRole(role);
						}
					}

					for(String role : alreadyRoles)
					{
						if(!p.getRoles().contains(role)){
							this.viewPlayers.get(p.getName()).getRoles().deleteRole(role);
						}
					}
				}
			}
		}

	}
}
