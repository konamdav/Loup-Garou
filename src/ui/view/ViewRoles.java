package ui.view;

import java.util.ArrayList;
import java.util.List;

public class ViewRoles {
	private List<ViewRole> roles;
	private ViewPlayer player;
	private int curX;
	private int curY;

	public ViewRoles(ViewPlayer player)
	{
		this.player = player;
		this.roles = new ArrayList<ViewRole>();
		
		curX = player.getPosition()[0] + 10;
		curY = player.getPosition()[1];	
		
		if(this.getPlayer().getDirection().equals("DOWN"))
		{
			curX = curX;
			curY = curY+20;
		}
		else if(this.getPlayer().getDirection().equals("LEFT"))
		{
			curX = curX +20;
			curY = curY;
		}
	}
	
	public void addNewRole(String role)
	{
		if(this.getPlayer().getDirection().equals("UP"))
		{
			curX = curX;
			curY = curY-40;
			this.roles.add(new ViewRole(this, role, curX, curY));
		}
		else if(this.getPlayer().getDirection().equals("DOWN"))
		{
			curX = curX;
			curY = curY+40;
			this.roles.add(new ViewRole(this, role, curX, curY));
		}
		
		else if(this.getPlayer().getDirection().equals("RIGHT"))
		{
			curX = curX-40;
			curY = curY;
			this.roles.add(new ViewRole(this, role, curX, curY));
		}
		else if(this.getPlayer().getDirection().equals("LEFT"))
		{
			curX = curX+40;
			curY = curY;
			this.roles.add(new ViewRole(this, role, curX, curY));
		}
	}

	public ViewPlayer getPlayer() {
		return player;
	}
	
	public void update()
	{
		for(ViewRole role : this.roles)
		{
			role.update();
		}
	}
	
}
