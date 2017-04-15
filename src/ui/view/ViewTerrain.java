package ui.view;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ui.control.Controleur_Terrain;
import ui.model.Case_Terrain;
import ui.model.Const;

public class ViewTerrain {

	private SpriteBatch batch;    
	private Texture case_constructible;
	
	private Controleur_Terrain ctrlTerrain;

	public ViewTerrain(SpriteBatch batch, Controleur_Terrain ctrlTerrain)
	{
		this.ctrlTerrain=ctrlTerrain;
		this.batch=batch;
		this.case_constructible = new Texture(Gdx.files.internal("resources/sprites/case_constructible.png"));	
	}


	public void update() 
	{
		for(int i = 0; i<Const.LIGNE ; i++)
		{
			for(int j = 0 ; j<Const.COLONNE ; j++)
			{
				if(ctrlTerrain.getSonTerrain().getMap()[i][j] instanceof Case_Terrain)
				{
					batch.draw(this.case_constructible,j*64,i*64);  
				}  
			}

		}  
	}
}
