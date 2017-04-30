package sma.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Grille de suspcicion
 * @author Davy
 *
 */
public class SuspicionScore 
{
	
	private Map<String, Integer> score;
	
	public SuspicionScore() {
		super();
		this.score = new HashMap<String, Integer>();
	}

	public int getScore(String name)
	{
		if(this.score.containsKey(name))
		{
			return this.score.get(name);
		}
		else
		{
			return 0;
		}
	}
	
	public void addScore(String name, int score)
	{
		if(this.score.containsKey(name))
		{
			this.score.put(name, Math.max(this.score.get(name),score)+50);
		}
		else
		{
			this.score.put(name, score);
		}
	}
	
	public void getScore()
	{
		this.score.clear();
	}
}
	

