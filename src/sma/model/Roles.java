package sma.model;

import java.util.ArrayList;
import java.util.List;

public class Roles {
	public static final String WEREWOLF = "WEREWOLF";
	public static final String CITIZEN = "CITIZEN";
	public static final String MAYOR = "MAYOR";
	public static final String LITTLE_GIRL = "LITTLE_GIRL";
	public static final String CUPID = "CUPID";
	public static final String WITCH = "WITCH";
	public static final String MEDIUM = "MEDIUM";
	public static final String LOVER = "LOVER";
	public static final String WHITE_WEREWOLF = "WHITE_WEREWOLF";
	public static final String ANGEL = "ANGEL";
	public static final String GREAT_WEREWOLF = "GREAT_WEREWOLF";
	public static final String HUNTER = "HUNTER";
	public static final String THIEF = "THIEF";
	public static final String EXORCIST = "EXORCIST";

	public static final String FOOL = "FOOL";
	public static final String FLUTE_PLAYER = "FLUTE_PLAYER";
	public static final String CHARMED = "CHARMED";
	public static final String  SCAPEGOAT= "SCAPEGOAT";
	public static final String GENERIC = "GENERIC";

	public static final String ANCIENT = "ANCIENT";
	public static final String SALVATOR = "SALVATOR";
	
	public static final String FAMILY = "FAMILY";

	
	public static List<String> getMainRoles()
	{
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(CITIZEN);
		tmp.add(WEREWOLF);
		tmp.add(Roles.LITTLE_GIRL);
		tmp.add(Roles.GREAT_WEREWOLF);
		tmp.add(Roles.WHITE_WEREWOLF);
		tmp.add(Roles.THIEF);
		tmp.add(Roles.ANCIENT);
		tmp.add(Roles.WITCH);
		tmp.add(Roles.MEDIUM);
		tmp.add(Roles.ANGEL);
		tmp.add(Roles.FAMILY);
		tmp.add(Roles.CUPID);
		tmp.add(Roles.HUNTER);
		tmp.add(Roles.SCAPEGOAT);
		
		return tmp;
	}
	
	//public static final String WILD = "WILD";
	//public static final String WILD = "EXORCIST";
	
}
