package ui.view;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import jade.lang.acl.ACLMessage;
import sma.model.DFServices;
import sma.model.GameSettings;
import ui.agent.uiAgent;

public class ViewJoinMenu implements Screen{

	App app;
	Stage stage;
	Skin skin;
	int i =0;
	private long time;

	public ViewJoinMenu(App a){

		app = a;
		this.time = 0;
		stage=new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));

		Image img = new Image(new Texture("resources/sprites/bg.png"));
        img.setPosition(0, 0);
        stage.addActor(img);

		app.agent.addQuery();

		Table table=new Table();
		table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		table.center();

		Label list_game_label =new Label("Partie en cours",skin);
		table.add(list_game_label);
		table.row();

		if (app.getContainers()!= null){
			for (int c : app.getContainers()){
				Label game_label =new Label("Partie " + c ,skin);
				table.add(game_label);

				TextButton join_button = new TextButton("Rejoindre",skin);
				table.add(join_button);
				table.row();

				final int id = c;
				join_button.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						//System.out.println("DEBOGAGE : " + i + "  --  " + app.getContainers().get(id) );
						app.agent.getInformations(id);
						app.setScreen(new ViewInterfaceGame(app));
					}
				});
			}
		}


		TextButton creer_button = new TextButton("Nouvelle partie",skin);
		table.add(creer_button);
		table.row();


		TextButton refresh_button = new TextButton("Rafraîchir",skin);
		table.add(refresh_button);
		table.row();

		stage.addActor(table);

		creer_button.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				app.setScreen(new ViewNewGameMenu(app));
			}
		});

		refresh_button.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				app.setScreen(new ViewJoinMenu(app));
			}
		});


	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(this.time == 0){
			this.time = System.currentTimeMillis();
		}

		
		if(System.currentTimeMillis() - this.time > 1000)
		{
			app.agent.addQuery();
			app.setScreen(new ViewJoinMenu(app));
			this.time = 0;
		}
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}


	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

}
