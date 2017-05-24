package ui.control;

import java.util.Random;

import ui.model.Case;
import ui.model.Const;
import ui.model.Map;
/***
 * Controleur Terrain
 * @author Davy
 *Gere tout ce qui est relatif au terrain
 */
public class MapController {
	private Map sonTerrain;
	private static int ii = 0;

	public  MapController() {
		super();
		preparerTerrain();
	}
	
	
	public Case getCase(double x, double y)
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
	
	public Case getCaseByXY(int i, int j)
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
	
	public Map getSonTerrain() {
		return sonTerrain;
	}

	public void preparerTerrain()
	{
		sonTerrain=new Map();
		new Random();
		int fichier=++ii %Const.NB_TERRAIN;
		sonTerrain.chargerCarte();
	}
	
}
