package sma.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonIgnore;

/***
 * Grille de suspcicion
 * @author Davy
 *
 */
public class SuspicionScore 
{
	//grille global de suspicion
	private Map<String, Integer> score;
	//archive player suspicion
	@JsonIgnore
	private Map<String, Map<String, Integer>> playerSuspicion;

	public SuspicionScore() {
		super();
		this.score = new HashMap<String, Integer>();
		this.playerSuspicion = new HashMap<String, Map<String, Integer>>();
	}

	public Map<String, Integer> getScore() {
		return score;
	}

	@JsonIgnore
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

	@JsonIgnore
	public void addSuspicionScoreGrid(String name, SuspicionScore scoreGrid)
	{
		this.playerSuspicion.put(name, scoreGrid.score);
		for(Entry<String, Integer> entry : scoreGrid.score.entrySet())
		{
			this.score.put(entry.getKey(), this.getScore(entry.getKey())+entry.getValue());
		}
	}
	@JsonIgnore
	public void addScore(String name, int score)
	{
		this.score.put(name, score + this.getScore(name));
	}
	
	@JsonIgnore
	public void clear()
	{
		this.score.clear();
		this.playerSuspicion.clear();
	}
}


