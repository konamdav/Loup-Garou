package sma.generic_vote;

import java.util.List;

public interface IVotingAgent {
	public List<String> getVotingBehaviours();
	public String getName();
	public int getGameid();
}
