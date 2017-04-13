package ui.view;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ui.control.Controleur_Terrain;
import ui.model.Case_Terrain;
import ui.model.Const;

public class ViewTerrain {

	private Group group;    
	private Image case_constructible;
	
	private Controleur_Terrain ctrlTerrain;


	public ViewTerrain(Group group, Controleur_Terrain ctrlTerrain)
	{
		this.ctrlTerrain=ctrlTerrain;
		this.group=group;
		this.group = group;
		this.case_constructible =   new Image("resources/sprites/case_constructible.png");
		update() ;
	}


	public void update() 
	{
		for(int i = 0; i<Const.LIGNE ; i++)
		{
			for(int j = 0 ; j<Const.COLONNE ; j++)
			{
				if(ctrlTerrain.getSonTerrain().getMap()[i][j] instanceof Case_Terrain)
				{
					ImageView caseTerrain = new ImageView(this.case_constructible);
					caseTerrain.setX(i*64);
					caseTerrain.setY(j*64);
					this.group.getChildren().add(caseTerrain);  
					
					System.out.println("...");
				}  
			}

		}  
	}
}
