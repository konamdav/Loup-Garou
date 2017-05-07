
package ui.view;
//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import sma.launch.SystemContainer;
import ui.control.Controleur_Terrain;


public class ViewInterfaceGame implements Screen{

	private Stage stage;
	private ViewMap terrain;
	private Controleur_Terrain ctrlTerrain;
	private ViewPlayers viewPlayers;
	private int iii;
	private Texture textureNight;
	boolean hover;

	private int mapx; 
	private int mapy;

	public ViewInterfaceGame (App game){
		this.ctrlTerrain = new Controleur_Terrain();
		hover = true;
		mapx = 0;
		mapy = 0;
	}


	public void show() {
		stage=new Stage(){
			@Override
			public boolean mouseMoved(int x, int y) {
				y=(int) (this.getHeight()-y);

				//System.out.println(" X = "+x+" Y = "+y);
				x = x+10;
				y = y+10;

				mapx = x;
				mapy = y;

				//survol_terrain(x,y);


				return true;
			}
		};

		Gdx.input.setInputProcessor(stage);
		viewPlayers=new ViewPlayers(((SpriteBatch)stage.getBatch()));
		terrain=new ViewMap((SpriteBatch) stage.getBatch(), ctrlTerrain);

		ViewPlayer player;

		initPlayers((int) (4+Math.random()*20));

		textureNight = new Texture(Gdx.files.internal("resources/sprites/night.png"));

		new Thread(
				new Runnable(){
					public void run(){
						
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Gdx.app.postRunnable(new Runnable(){

							@Override
							public void run() {

								new SystemContainer();
								Skin uiSkin = new Skin(Gdx.files.internal("resources/visui/uiskin.json"));
								stage.addActor(new Label("test", uiSkin ));
							}

						});
					}}).start();;

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
		viewPlayers.drawPlayersDead();
		viewPlayers.drawPlayersSleep();

		if(iii > 100)
		{
			stage.getBatch().draw(textureNight,0,0);
			if(iii == 101) 	this.viewPlayers.sleep();

		}

		++iii;

		if(iii> 200)
		{
			iii = 0;
			this.viewPlayers.wake();
			this.viewPlayers.dead();
		}

		viewPlayers.drawPlayersWake();


		if(hover)
		{
			BitmapFont  font = new BitmapFont();
			font.setColor(Color.WHITE);
			font.setScale(1.5f);
			font.draw(stage.getBatch(), viewPlayers.getLabel(mapx, mapy), mapx, mapy);
		}

		//stage.getBatch().draw(style.getBackground(), 0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.getBatch().end(); 
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.getBatch().disableBlending();
	}


	public void hide() {
	}


	public void pause() {

	}


	public void resize(int arg0, int arg1) {

	}


	public void initPlayers(int nb)
	{
		System.out.println("NB " +nb);
		int n_rows = 0;
		int n_cols = 0 ;

		n_rows  = nb % 4;
		n_cols = nb / 4;

		System.out.println("NC "+n_cols+" NR "+n_rows);

		if(n_cols+n_rows >= 2)
		{
			if(nb>4)
			{
				n_cols = (nb-4)/2 + (nb-4)%2;
				//n_cols+= n_cols-2+n_rows;
				n_rows = 2;
			}
		}
		else
		{
			n_rows = n_cols;
		}

		System.out.println("NC "+n_cols+" NR "+n_rows);

		int ind_x = 6;
		int ind_y = 4;
		ViewPlayer player;
		for(int i = 0; i<n_rows; ++i)
		{
			player = viewPlayers.newPlayer("PLAYER1"+i,"WAKE", "RIGHT", ind_x-n_cols/2, ind_y-(n_rows-1-i) );
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}

		if(nb >= 2) {
			int ret = 0;
			if(n_cols%2!=0)
			{
				ret = 1;
			}

			for(int i = 0; i<n_rows; ++i)
			{
				player = viewPlayers.newPlayer("PLAYER3"+i,"WAKE", "LEFT", ind_x+1+ret+n_cols/2, ind_y-i );
				if((int)(Math.random()*3) == 1){
					player.getRoles().addNewRole("WEREWOLF");
				}
				else
				{
					player.getRoles().addNewRole("CITIZEN");
				}

			}

			if(nb >= 3){

				for(int i = 0; i<n_cols; ++i)
				{
					player = viewPlayers.newPlayer("PLAYER2"+i,"WAKE", "DOWN", ind_x+1+i-n_cols/2, ind_y+1);
					if((int)(Math.random()*3) == 1){
						player.getRoles().addNewRole("WEREWOLF");
					}
					else
					{
						player.getRoles().addNewRole("CITIZEN");
					}

				}

				if(nb >= 4){
					int reste = nb - (n_cols+2*n_rows);
					for(int i = 0; i<reste; ++i)
					{
						player = viewPlayers.newPlayer("PLAYER4"+i,"WAKE", "UP", ind_x+1+(n_cols-1-i)-n_cols/2, ind_y-1-n_rows/2);
						if((int)(Math.random()*3) == 1){
							player.getRoles().addNewRole("WEREWOLF");
						}
						else
						{
							player.getRoles().addNewRole("CITIZEN");
						}

					}
				}
			}
		}

	}

	public void resume() {

	}
}