package ui.view;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ui.control.MapController;
import ui.model.Case;
import ui.model.Const;

public class ViewMap {

	private SpriteBatch batch;    
	private Texture case1;
	private Texture case2;
	
	private MapController ctrlTerrain;

	public ViewMap(SpriteBatch batch, MapController ctrlTerrain)
	{
		this.ctrlTerrain=ctrlTerrain;
		this.batch=batch;
		this.case1 = new Texture(Gdx.files.internal("resources/sprites/case_constructible.png"));	
		this.case2 = new Texture(Gdx.files.internal("resources/sprites/case_tree.png"));	
	}


	public void update() 
	{
		for(int i = 0; i<Const.LIGNE ; i++)
		{
			for(int j = 0 ; j<Const.COLONNE ; j++)
			{
				if(ctrlTerrain.getSonTerrain().getMap()[i][j].getT() == 0)
				{
					batch.draw(this.case1,j*64,i*64);  
				}  
				else if (ctrlTerrain.getSonTerrain().getMap()[i][j].getT() == 1)
				{
					batch.draw(this.case2,j*64,i*64);  
				}  
			}

		}  
	}
}
