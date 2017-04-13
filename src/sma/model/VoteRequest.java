package sma.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import jade.core.AID;

public class VoteRequest {
	private String request;
	private List<String> choices;
	private List<String> voters;
	private VoteResults globalVoteResults;
	
	public VoteRequest() {
		super();
		this.choices = new ArrayList<String>();
		this.voters = new ArrayList<String>();
		this.globalVoteResults = new VoteResults();
		this.request="UNKNOWN_REQUEST";
	}
	
	public VoteRequest(List<String> choices) {
		super();
		this.choices = choices;
		this.voters = new ArrayList<String>();
		this.request="UNKNOWN_REQUEST";
		this.globalVoteResults = new VoteResults();
	}
	
	public VoteRequest(List<String> choices , VoteResults results) {
		super();
		this.choices = choices;
		this.globalVoteResults = results;
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
	
	
}
