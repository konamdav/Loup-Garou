package ui.model;
import java.io.InputStream;
import java.util.Scanner;

public class Terrain{
	/***map**/
	private Case_Terrain[][] map;

	public Case_Terrain[][] getMap() {
		return map;
	}

	public Terrain()
	{
		map = new Case_Terrain[Const.LIGNE][Const.COLONNE];
		
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
					map[i][j]=new Case_Terrain(i, j);	
				}

			}

		} finally {
			if (sc != null)
				sc.close();
		}
	}
}
