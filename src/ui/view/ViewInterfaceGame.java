
package ui.view;
import java.util.ArrayList;
//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import sma.model.DFServices;
import sma.model.PlayerProfile;
import sma.model.Roles;
import sma.model.Status;
import ui.control.MapController;
import ui.model.ViewPlayer;
import ui.model.ViewPlayers;



public class ViewInterfaceGame implements Screen{
	private Stage stage;
	private ViewMap terrain;
	private MapController ctrlTerrain;
	private ViewPlayers viewPlayers;
	private Texture textureNight;
	boolean hover;

	private int mapx; 
	private int mapy;
	private App game;
	protected java.util.List<PlayerProfile> list;

	public ViewInterfaceGame (App game){
		this.ctrlTerrain = new MapController();
		hover = true;
		mapx = 0;
		mapy = 0;
		this.game = game;
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
		//Gdx.graphics.setDisplayMode(1194, 574, false);
		//game.resize(1024, 555);

		Gdx.input.setInputProcessor(stage);
		viewPlayers=new ViewPlayers(((SpriteBatch)stage.getBatch()));

		/* Iiaison UI */

		//game.agent.



		/** test**/
		/*
		ArrayList<PlayerProfile> profiles = new ArrayList<PlayerProfile>();
		for(int i = 0; i<22; ++i)
		{
			PlayerProfile p = new PlayerProfile();
			p.setName("PLAYER "+i);
			p.setStatus(Status.WAKE);
			ArrayList<String> roles = new ArrayList<String>();
			if(Math.random()%3 == 1)
			{
				roles.add(Roles.WEREWOLF);
			}
			else
			{
				if(Math.random()%3 == 1)
				{
					roles.add(Roles.LOVER);
				}
				else
				{
					roles.add(Roles.ANGEL);
				}

				roles.add(Roles.CITIZEN);
			}

			p.setRoles(roles);
			profiles.add(p);

			Skin uiskin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));

			SelectBox<String> list = new SelectBox<String>(uiskin);


			list.setItems("test", "dk", "konam","ok");
			list.setSelected("ok");
			list.pack();
			stage.addActor(list);
		}
		try {
			Thread.sleep(000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		list = DFServices.getPlayerProfiles(this.game.agent, 0);
		this.viewPlayers.updatePlayers(list);
		 */

		/*
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				System.out.println("oooo");
				Gdx.app.postRunnable(new Runnable(){
					@Override
					public void run() {


					}	
				});
			}
		}, 1000, 1000);


		 */
		terrain=new ViewMap((SpriteBatch) stage.getBatch(), ctrlTerrain);

		ViewPlayer player;

		//initPlayers((int) (4+Math.random()*20));

		textureNight = new Texture(Gdx.files.internal("resources/sprites/night.png"));

	}


	public void dispose() {
		this.dispose();
		stage.dispose();
	}

	public void render(float arg0)
	{

		//System.gc();
		//Pour actualiser l'interface
		Gdx.gl.glClearColor(0.7f,0.7f,0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);       
		stage.getBatch().enableBlending();
		stage.getBatch().begin();

		terrain.update();		
		viewPlayers.drawPlayersDead();
		viewPlayers.drawPlayersSleep();



		if(this.game.getGameInformations()!=null){
			viewPlayers.updatePlayers(this.game.gameInformations.getProfiles());
		}
		
		viewPlayers.drawPlayersWake();


		if(hover)
		{
			BitmapFont  font = new BitmapFont();
			font.setColor(Color.WHITE);
			font.setScale(1.5f);
			font.drawMultiLine(stage.getBatch(), viewPlayers.getLabel(mapx, mapy), mapx, mapy);
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



	public void resume() {

	}
}