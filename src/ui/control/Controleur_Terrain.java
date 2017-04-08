package ui.control;

import java.util.Random;

import ui.model.Case_Terrain;
import ui.model.Const;
import ui.model.Terrain;
/***
 * Controleur Terrain
 * @author Davy
 *Gere tout ce qui est relatif au terrain
 */
public class Controleur_Terrain {
	private Terrain sonTerrain;
	private static int ii = 0;

	public  Controleur_Terrain() {
		super();
		preparerTerrain();
	}
	
	
	public Case_Terrain getCase(double x, double y)
	{
		int i=(int) (x/Const.TAILLE_CASE);
		int j=(int) (y/Const.TAILLE_CASE);
	
		if(i>=0&&j>=0&&i<Const.COLONNE&&j<Const.LIGNE)
		{
			
			return sonTerrain.getMap()[j][i];
		}
		else
		{
			return null;
		}
	}
	
	public Case_Terrain getCaseByXY(int i, int j)
	{	
		if(i>=0&&j>=0&&i<Const.COLONNE&&j<Const.LIGNE)
		{
			
			return sonTerrain.getMap()[j][i];
		}
		else
		{
			return null;
		}
	}
	
	public Terrain getSonTerrain() {
		return sonTerrain;
	}

	public void preparerTerrain()
	{
		sonTerrain=new Terrain();
		new Random();
		int fichier=++ii %Const.NB_TERRAIN;
		sonTerrain.chargerCarte();
	}
	
}
