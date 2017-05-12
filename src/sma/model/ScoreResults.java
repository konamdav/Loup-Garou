package sma.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonIgnore;

public class ScoreResults {

	private Map<String, Integer> results;
	
	public ScoreResults() {
		super();
		this.results = new HashMap<String, Integer>();
	}

	public ScoreResults(Map<String, Integer> results) {
		super();
		this.results = results;
	}

	public Map<String, Integer> getResults() {
		return results;
	}

	public void ListResults(Map<String, Integer> results) {
		this.results = results;
	}
	
	public void add(ScoreResults voteResult)
	{
		for(Entry<String, Integer> entry : voteResult.getResults().entrySet())
		{
			if(results.containsKey(entry.getKey()))
			{
				results.put(entry.getKey(), entry.getValue()+results.get(entry.getKey()));
			}
			else
			{
				results.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public List<String> getFinalResults()
	{
		ArrayList<String> finalResults = new ArrayList<String>();
		int m = Integer.MIN_VALUE;
		for(Entry<String, Integer> entry : getResults().entrySet())
		{
			if(m == entry.getValue())
			{
				finalResults.add(entry.getKey());
			}
			else if(m < entry.getValue())
			{
				finalResults.clear();
				m = entry.getValue();
				finalResults.add(entry.getKey());

			}
		}
		return finalResults;
	}
	
	@JsonIgnore
	public int getMaxScore()
	{
		ArrayList<String> finalResults = new ArrayList<String>();
		int m = Integer.MIN_VALUE;
		for(Entry<String, Integer> entry : getResults().entrySet())
		{
			if(m < entry.getValue())
			{
				m = entry.getValue();
			}
		}
		return m;
	}
	
}
