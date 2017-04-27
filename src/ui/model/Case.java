package ui.model;

public  class Case 
{
	protected int x;
	protected int y;
	

	public Case(int x, int y)
	{
		this.x=x;
		this.y=y;
		
	}
	
	
	public int getX()
	{
		return(x);
	}
	
	
	public int getX_Abs()
	{
		return x*Const.TAILLE_CASE;
	}
	
	public int getY()
	{
		return(y);
	}
	
	
	public int getY_Abs()
	{
		return y*Const.TAILLE_CASE;
	}
	

	
	
}
