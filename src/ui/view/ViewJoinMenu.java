package ui.view;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import jade.lang.acl.ACLMessage;
import sma.model.DFServices;
import sma.model.GameSettings;

public class ViewJoinMenu implements Screen{
	
	App app;
    Stage stage;
    Skin skin;
    int i =0;
	
	public ViewJoinMenu(App a){
		app = a;
		stage=new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));
        
        app.agent.addQuery();

        Table table=new Table();
        table.setSize(300,600);
        table.center();
        
        
        Label list_game_label =new Label("Partie en cours",skin);
        table.add(list_game_label);
        table.row();
        
        
        
        TextButton creer_button = new TextButton("Nouvelle partie",skin);
        table.add(creer_button);
        table.row();
		
        stage.addActor(table);
        
        creer_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	/*
            	TextButton bbu = new TextButton("Créer"+i,skin);
            	bbu.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                    	TextButton bbu = new TextButton("ok"+this.getButton(),skin);
                        table.add(bbu);
                        table.row();
                    }
                });
                table.add(bbu);
                table.row();
                i++;*/

        		app.setScreen(new ViewNewGameMenu(app));
            }
        });
        
        
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
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
