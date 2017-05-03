
package ui.view;
//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ui.control.Controleur_Terrain;

public class StartView implements Screen
{
	private Stage stage;
	private App app;
	private Table tableLevel2;
	private Skin skin;
	private VerticalGroup v;

	private int i = 0;

	public StartView (App game){
		this.app = game;
	}


	public void show() {
		stage=new Stage();
		
		System.out.println("creation");
		
		
		skin = new Skin(Gdx.files.internal("resources/visui/uiskin.json"));
		Gdx.input.setInputProcessor(stage);

		// Initialize
		v = new VerticalGroup();
		
		final Button btnStartServer = new TextButton("Start game server", skin);
		final Button btnStopServer = new TextButton("Stop game server", skin);
		btnStopServer.setDisabled(true);
		
		btnStartServer.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	            System.out.println("Start server");
	            new Thread(new Runnable(){
	    			@Override
	    			public void run() {
	    				btnStopServer.setDisabled(false);
	    				StartView.this.app.newSystemContainer();
	    				
	    			}}).start();;
	        }
	    });
		
		
		final TextArea text = new TextArea("127.0.0.1", skin);
		final Button btnJoinServer = new TextButton("Join game server", skin);
	
	
		
		
		btnStopServer.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	            System.out.println("Start stop");
	            new Thread(new Runnable(){
	    			@Override
	    			public void run() {
	    				
	    				System.out.println(" StartView.this.app.getSystemContainer() "+StartView.this.app.getSystemContainer());
	    				StartView.this.app.getSystemContainer();
	    				StartView.this.app.getSystemContainer().stop();				
	    			}}).start();;
	        }
	    });
		
		
		
		btnJoinServer.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	            System.out.println("Start stop");
	            app.setScreen(new ViewInterfaceGame(app));
	        }
	    });
		v.addActor(btnStartServer);
		
		v.addActor(btnStopServer);
		v.pack();
		//v.fill();
		v.addActor(text);
		v.addActor(btnJoinServer);


		// Add to the 'top'
		v.addActorAt(0, new Label("I'm at the first row", skin));
		v.pack();
		// etc.

		// ScrollPane
		ScrollPane scrollPane = new ScrollPane(v, skin);
		scrollPane.layout();
		//	scrollPane.addActor(v);

		//	Table table3 = new Table(skin);	// 
		//table3.add(scrollPane).padLeft(220).expandY();

		//scrollPane.set
		scrollPane.setVisible(true);
		scrollPane.setFadeScrollBars(true);
		scrollPane.setForceScroll(false, true);
		scrollPane.setFlickScroll(true);
		scrollPane.setWidth(900);
		scrollPane.setScrollY(20);
		scrollPane.setScrollPercentY(50);
		scrollPane.updateVisualScroll();
		scrollPane.layout();
		stage.addActor(scrollPane);
		//stage.act();
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


		//v.addActorAt(0, new Label("I'm at the first row"+(++i), skin));
		//v.pack();

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