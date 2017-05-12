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
	private VoteResults globalCitizenVoteResults;
	private VoteResults localVoteResults;
	private SuspicionScore collectiveSuspicionScore;
	private boolean canBeFake;
	private boolean askRequest;
	
	public VoteRequest() {
		super();
		this.choices = new ArrayList<String>(); //String convert to aid later
		this.voters = new ArrayList<String>();//String convert to aid later
		this.globalCitizenVoteResults = new VoteResults();// Archive des votes previously on AMC WALKING DEAD
		this.localVoteResults = new VoteResults();
		this.collectiveSuspicionScore = new SuspicionScore();
		this.request="UNKNOWN_REQUEST";
		this.voteAgainst = true;
		this.canBeFake = false;
		this.askRequest = false;
	}
	
	public VoteRequest(List<String> choices) {
		super();
		this.choices = choices;
		this.voters = new ArrayList<String>();
		this.request="UNKNOWN_REQUEST";
		this.globalCitizenVoteResults = new VoteResults();
		this.localVoteResults = new VoteResults();
		this.collectiveSuspicionScore = new SuspicionScore();
		this.voteAgainst = true;
		this.askRequest = false;
	}
	
	public VoteRequest(List<String> choices , VoteResults results) {
		super();
		this.choices = choices;
		this.globalCitizenVoteResults = results;
		this.request="UNKNOWN_REQUEST";
		this.localVoteResults = new VoteResults();
		this.collectiveSuspicionScore = new SuspicionScore();
		this.voteAgainst = true;
		this.askRequest = false;
	}
	
	public VoteRequest(List<String> choices , VoteResults gresults, VoteResults lresults) {
		super();
		this.choices = choices;
		this.request="UNKNOWN_REQUEST";
		this.globalCitizenVoteResults = gresults;
		this.localVoteResults = lresults;
		this.collectiveSuspicionScore = new SuspicionScore();
		this.voteAgainst = true;
		this.askRequest = false;
	}

	public boolean isAskRequest() {
		return askRequest;
	}

	public void setAskRequest(boolean askRequest) {
		this.askRequest = askRequest;
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

	public VoteResults getGlobalCitizenVoteResults() {
		return globalCitizenVoteResults;
	}

	public void setGlobalCitizenVoteResults(VoteResults globalVoteResults) {
		this.globalCitizenVoteResults = globalVoteResults;
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
	
	public boolean isCanBeFake() {
		return canBeFake;
	}

	public void setCanBeFake(boolean canBeFake) {
		this.canBeFake = canBeFake;
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

	public SuspicionScore getCollectiveSuspicionScore() {
		return collectiveSuspicionScore;
	}

	public void setCollectiveSuspicionScore(SuspicionScore collectiveSuspicionScore) {
		this.collectiveSuspicionScore = collectiveSuspicionScore;
	}	
}
