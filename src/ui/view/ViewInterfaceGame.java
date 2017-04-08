
package ui.view;
//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ui.control.Controleur_Terrain;




public class ViewInterfaceGame implements Screen{

	private Stage stage;
	private ViewTerrain terrain;
	private Controleur_Terrain ctrlTerrain;
	private ViewPlayers graphique_monstres;
	private int iii;
	private Texture textureNight;

	public ViewInterfaceGame (App game){
		this.ctrlTerrain = new Controleur_Terrain();
	}


	public void show() {
		stage=new Stage();

		graphique_monstres=new ViewPlayers(((SpriteBatch)stage.getBatch()));
		terrain=new ViewTerrain((SpriteBatch) stage.getBatch(), ctrlTerrain);

		ViewPlayer player;


		for(int i = 3; i<5; ++i)
		{
			player = graphique_monstres.newPlayer("player 3"+i,"WAKE", "RIGHT", 2, i);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}

		for(int i = 3; i<5; ++i)
		{
			player = graphique_monstres.newPlayer("player 4"+i,"WAKE", "LEFT", 12, i);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}
		}

		for(int i = 3; i<12; ++i)
		{
			player = graphique_monstres.newPlayer("player 1"+i,"WAKE", "DOWN", i, 5);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}


		for(int i = 3; i<12; ++i)
		{
			player = graphique_monstres.newPlayer("player 2"+i, "WAKE","UP", i, 2);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}

		textureNight = new Texture(Gdx.files.internal("resources/sprites/night.png"));

	}


	public void dispose() {
		this.dispose();
		stage.dispose();
	}

	public void render(float arg0)
	{
		//Pour actualiser l'interface
		Gdx.gl.glClearColor(0.7f,0.7f,0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);       
		stage.getBatch().enableBlending();
		stage.getBatch().begin();

		terrain.update();		
		graphique_monstres.drawPlayersDead();
		graphique_monstres.drawPlayersSleep();

		if(iii > 100)
		{
			stage.getBatch().draw(textureNight,0,0);
			if(iii == 101) 	this.graphique_monstres.sleep();

		}

		++iii;

		if(iii> 200)
		{
			iii = 0;
			this.graphique_monstres.wake();
		}
		
		graphique_monstres.drawPlayersWake();


		//stage.getBatch().draw(style.getBackground(), 0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getBatch().end(); 
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.getBatch().disableBlending();
	}


	public void hide() {
		// TODO Auto-generated method stub

	}


	public void pause() {

	}


	public void resize(int arg0, int arg1) {

	}


	public void resume() {
		// TODO Auto-generated method stub

	}
}