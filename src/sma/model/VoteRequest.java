package sma.model;

import java.util.ArrayList;
import java.util.List;

public class VoteRequest {
	private String request;
	private List<String> choices;
	private VoteResults globalVoteResults;
	
	public VoteRequest() {
		super();
		this.request="UNKNOWN_REQUEST";
		this.choices = new ArrayList<String>();
		this.globalVoteResults = new VoteResults();
	}
	
	public VoteRequest(List<String> choices) {
		super();
		this.choices = choices;
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

	
	
	
	
}
