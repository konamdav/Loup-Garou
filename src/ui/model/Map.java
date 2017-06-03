package ui.model;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Map{
	/***map**/
	private Case[][] map;

	public Case[][] getMap() {
		return map;
	}

	public Map()
	{
		map = new Case[Const.LIGNE][Const.COLONNE];
		
	}
	
	public void chargerCarte()
	{
		String chemin="resources/terrain/map.txt";
		InputStream carte=getClass().getClassLoader().getResourceAsStream(chemin);
		
		Scanner sc = null;
		try {
			sc = new Scanner(carte);

			for(int i=0; i<Const.LIGNE;i++)
			{
				for(int j=0; j<Const.COLONNE;j++)
				{
					int t = (j==0 || j== Const.COLONNE-1 || i == 0 || i == Const.LIGNE-1) ? 1 : 0;
					map[i][j]=new Case(i, j, t);	
				}
			}
			try {
				carte.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			if (sc != null)
				sc.close();
		}
	}
}
