package sma.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import jade.core.AID;

public class VoteRequest {
	private String request;
	private boolean voteAgainst;
	private List<String> choices;
	private List<String> voters;
	private VoteResults globalVoteResults;
	private VoteResults localVoteResults;
	
	public VoteRequest() {
		super();
		this.choices = new ArrayList<String>(); //String convert to aid later
		this.voters = new ArrayList<String>();//String convert to aid later
		this.globalVoteResults = new VoteResults();// Archive des votes previously on AMC WALKING DEAD
		this.localVoteResults = new VoteResults();
		this.request="UNKNOWN_REQUEST";
		this.voteAgainst = true;
	}
	
	public VoteRequest(List<String> choices) {
		super();
		this.choices = choices;
		this.voters = new ArrayList<String>();
		this.request="UNKNOWN_REQUEST";
		this.globalVoteResults = new VoteResults();
		this.voteAgainst = true;
	}
	
	public VoteRequest(List<String> choices , VoteResults results) {
		super();
		this.choices = choices;
		this.globalVoteResults = results;
		this.localVoteResults = new VoteResults();
		this.voteAgainst = true;
	}
	
	public VoteRequest(List<String> choices , VoteResults gresults, VoteResults lresults) {
		super();
		this.choices = choices;
		this.globalVoteResults = gresults;
		this.localVoteResults = lresults;
		this.voteAgainst = true;
	}

	public boolean isVoteAgainst() {
		return voteAgainst;
	}

	public void setVoteAgainst(boolean voteAgainst) {
		this.voteAgainst = voteAgainst;
	}

	public VoteResults getLocalVoteResults() {
		return localVoteResults;
	}

	public void setLocalVoteResults(VoteResults localVoteResults) {
		this.localVoteResults = localVoteResults;
	}

	public List<String> getChoices() {
		return choices;
	}

	public void setChoices(List<String> choices) {
		this.choices = choices;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public VoteResults getGlobalVoteResults() {
		return globalVoteResults;
	}

	public void setGlobalVoteResults(VoteResults globalVoteResults) {
		this.globalVoteResults = globalVoteResults;
	}

	public List<String> getVoters() {
		return voters;
	}

	public void setVoters(List<String> voters) {
		this.voters = voters;
	}
	
	@JsonIgnore
	public List<AID> getAIDVoters() {
		
		List<AID>  list = new ArrayList<AID>();
		for(String s : voters)
		{
			list.add(new AID(s));
		}
				
		return list;
	}
	
	@JsonIgnore
	public List<AID> getAIDChoices() {
		
		List<AID>  list = new ArrayList<AID>();
		for(String s : choices)
		{
			list.add(new AID(s));
		}
				
		return list;
	}
	
	
}
