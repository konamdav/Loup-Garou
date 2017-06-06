package ui.model;

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

	public String getAllRolesLabel()
	{
		String s ="";
		for(ViewRole role : this.roles)
		{
			s+="\n"+role.getName();
		}

		return s;
	}

	public void update()
	{
		int x = 0, y =0;
		if(!this.roles.isEmpty())
		{
			x = this.roles.get(0).getX();
			y = this.roles.get(0).getY();
			
			this.roles.get(0).update();
		}

		for(int i = 1; i<this.roles.size(); ++i)
		{			
			if(this.getPlayer().getDirection().equals("UP"))
			{
				x = x;
				y = y-40;
			}
			else if(this.getPlayer().getDirection().equals("DOWN"))
			{
				x = x;
				y = y+40;
				
			}

			else if(this.getPlayer().getDirection().equals("RIGHT"))
			{
				x = x-40;
				y = y;
				
			}
			else if(this.getPlayer().getDirection().equals("LEFT"))
			{
				x = x+40;
				y = y;
				
			}
			
			this.roles.get(i).setX(x);
			this.roles.get(i).setY(y);
			this.roles.get(i).update();
		}
	}

	public List<String> getListRole()
	{
		ArrayList<String> tmp = new ArrayList<String>();
		for(ViewRole role : this.roles)
		{
			tmp.add(role.getName());
		}

		return tmp;
	}

	public void deleteRole(String role) {
		boolean flag = false;
		int cpt = 0;

		int index = 0;
		int x = 0;
		int y = 0;
		while(cpt < this.roles.size())
		{
			if(flag)
			{
				int xx = this.roles.get(cpt).getX();
				int yy = this.roles.get(cpt).getY();

				this.roles.get(cpt).setX(x);
				this.roles.get(cpt).setY(y);

				curX = xx;
				curY = yy;

				x = xx;
				y = yy;
			}

			if(this.roles.get(cpt).getName().equals(role))
			{
				flag = true;
				x = this.roles.get(cpt).getX();
				y = this.roles.get(cpt).getY();
				index = cpt;
			}

			++cpt;
		}

		if(flag)
		{
			this.roles.remove(index);
		} 


	}

}
