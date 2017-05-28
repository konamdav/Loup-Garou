package sma.model;

/**
 * Message de vote forcé
 * @author Davy
 *
 */
public class ForceVoteRequest {
	private String voteRequest;
	private String voteResult;
	
	public String getVoteRequest() {
		return voteRequest;
	}
	public void setVoteRequest(String voteRequest) {
		this.voteRequest = voteRequest;
	}
	public String getVoteResult() {
		return voteResult;
	}
	public void setVoteResult(String voteResult) {
		this.voteResult = voteResult;
	}
	public ForceVoteRequest() {
		super();
	}
	
	
	
	
}
