package ui.view;

import java.io.IOException;

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
import sma.launch.GameContainer;
import sma.model.DFServices;
import sma.model.GameSettings;
import ui.sma.UIContainer;

public class ViewMainMenu implements Screen {
	
	App app;
    Stage stage;
    Skin skin;
    String test = "test";
    Label l ;

	public ViewMainMenu(App game){
		app = game;
		stage=new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));

        Table table=new Table();
        table.setSize(300,600);
        table.center();
        
        
        Label creer_label =new Label("Créer serveur de partie",skin);
        table.add(creer_label);
        table.row();
        
        TextField creer_port_textField = new TextField("PORT",skin);
        table.add(creer_port_textField);
        table.row();
        
        
        TextButton creer_button = new TextButton("Créer",skin);
        table.add(creer_button);
        table.row();

        Label rejoindre_label =new Label("Rejoindre serveur de partie",skin);
        table.add(rejoindre_label);
        table.row();
        
        TextField rejoindre_ip_textField = new TextField("IP",skin);
        table.add(rejoindre_ip_textField);
        table.row();
        
        TextField rejoindre_port_textField = new TextField("PORT",skin);
        table.add(rejoindre_port_textField);
        table.row();
        
        TextButton rejoindre_button = new TextButton("Rejoindre",skin);
        table.add(rejoindre_button);
        table.row();
        
      

        

        stage.addActor(table);
        
        creer_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	
            	
        		app.setScreen(new ViewNewGameMenu(app));
            }
        });
        
        rejoindre_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	test = "ouiiiiii";
            }
        });
        
        
	}

	@Override
	public void render(float delta) {
	
		Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}
	
}
