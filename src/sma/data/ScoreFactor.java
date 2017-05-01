package sma.data;

/***
 * Données de scoring
 * @author Davy
 *
 */
public class ScoreFactor {
	public static final int SCORE_FACTOR_WEREWOLF_VOTE = 250;
	public static final int SCORE_FACTOR_LOVER_VOTE = 250;
	
	public static int SCORE_MIN = Integer.MIN_VALUE/10;
	public static int SCORE_MAX = Integer.MAX_VALUE /10;
	
	public static int SCORE_FACTOR_GLOBAL_VOTE = 100;
	public static int SCORE_FACTOR_DIFFERENCE_GLOBAL_VOTE = 150;
	public static int SCORE_FACTOR_LOCAL_VOTE = 500;
	public static int SCORE_FACTOR_DIFFERENCE_LOCAL_VOTE = 250;
	
	public static int SCORE_FACTOR_LOCAL_NB_VOTE = 200;
	
	public static int SCORE_FACTOR_SUSPICION_MAX = 100000;
	public static int SCORE_FACTOR_SUSPICION_WEREWOLF = 500;
	public static int SCORE_FACTOR_SUSPICION_LITTLE_GIRL = 500;
	public static int SCORE_FACTOR_SUSPICION_DEFAULT = 300;
	public static int SCORE_FACTOR_SUSPICION_MIN = -100000;
	
}
