package sma.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonIgnore;

public class VoteResults {

	private Map<String, List<String>> voteResults;
	public VoteResults() {
		super();
		this.voteResults = new HashMap<String, List<String>>();
	}

	public VoteResults(Map<String, List<String>> voteResults) {
		super();
		
		this.voteResults = voteResults;
	}

	public Map<String, List<String>> getVoteResults() {
		return voteResults;
	}
	
	@JsonIgnore
	public Map<String, Integer> getSimpleVoteResults() {
		Map<String, Integer> simpleVoteResults = new HashMap<String, Integer>();
		for(Entry<String, List<String>> entry : getVoteResults().entrySet())
		{
			simpleVoteResults.put(entry.getKey(), entry.getValue().size());
		}
		return simpleVoteResults;
	}

	public void setVoteResults(Map<String, List<String>> voteResults) {
		this.voteResults = voteResults;
	}
	
	public void add(VoteResults newVoteResults)
	{
		for(Entry<String, List<String>> entry : newVoteResults.getVoteResults().entrySet())
		{
			List<String> voteResult = entry.getValue();
			
			if(this.voteResults.containsKey(entry.getKey()))
			{
				this.voteResults.get(entry.getKey()).addAll(voteResult);
			}
			else
			{
				this.voteResults.put(entry.getKey(),voteResult);
			}
		}
	}
	
	public int getVoteCount(String voted, String voter)
	{
		if(!this.voteResults.containsKey(voted)) return 0;
		
		return Collections.frequency(this.voteResults.get(voted), voter);
	}
	
	public List<String> getFinalResults()
	{
		ArrayList<String> finalResults = new ArrayList<String>();
		int m = Integer.MIN_VALUE;
		for(Entry<String, Integer> entry : getSimpleVoteResults().entrySet())
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
}
