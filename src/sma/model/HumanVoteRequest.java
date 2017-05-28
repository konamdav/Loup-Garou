package sma.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import jade.core.AID;

/**
 * Message de requete pour le vote humain
 * @author Davy
 *
 */
public class HumanVoteRequest {
	public HumanVoteRequest() {
		super();
	}

	private String player;
	private VoteRequest request;
	
	public HumanVoteRequest(String player, VoteRequest request) {
		super();
		this.player = player;
		this.request = request;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public VoteRequest getRequest() {
		return request;
	}
	public void setRequest(VoteRequest request) {
		this.request = request;
	}
	
	@JsonIgnore
	public AID getAIDPlayer() {
		return new AID(player);
	}
	
	
}
