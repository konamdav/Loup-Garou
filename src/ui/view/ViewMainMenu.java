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
    TextField rejoindre_ip_textField;
    
	public ViewMainMenu(App game){
		app = game;
		stage=new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));

        Image img = new Image(new Texture("resources/sprites/bg.png"));
        img.setPosition(0, 0);
        stage.addActor(img);
       
        Table table=new Table();
        table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.center();
        
        Image image = new Image(new Texture("resources/sprites/logo.png"));
        table.add(image);
        table.row();
        
        Label creer_label =new Label("Cr�er serveur de partie",skin);
        table.add(creer_label);
        table.row();
        
        TextField creer_port_textField = new TextField("PORT",skin);
        table.add(creer_port_textField);
        table.row();
        
        
        TextButton creer_button = new TextButton("Cr�er",skin);
        table.add(creer_button);
        table.row();

        Label rejoindre_label =new Label("Rejoindre serveur de partie",skin);
        table.add(rejoindre_label);
        table.row();
        
        rejoindre_ip_textField = new TextField("IP",skin);
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
            	app.newSystemContainer();
            	try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	app.newUIContainer("127.0.0.1");
        		app.setScreen(new ViewJoinMenu(app));
            }
        });
        
        rejoindre_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	app.newUIContainer(rejoindre_ip_textField.getText());
        		app.setScreen(new ViewJoinMenu(app));
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
