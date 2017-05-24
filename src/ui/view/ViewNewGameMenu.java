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
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import jade.lang.acl.ACLMessage;
import sma.model.DFServices;
import sma.model.GameSettings;

public class ViewNewGameMenu implements Screen {

	App app;
    Stage stage;
    Skin skin;
	
    TextField werewolf_textField;
    TextField citizen_textField;
    TextField lover_textField;
    TextField family_textField;
    TextField medium_textField;
    TextField little_girl_textField;
    
    
	ViewNewGameMenu(App a){
		app = a;
    	//app.newSystemContainer();
		//Gdx.graphics.setDisplayMode(800, 500, false);
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));
		
		Table table=new Table();
//        table.setSize(800,500);
  
		 table.setSize(300,600);
	       table.center();
        
        
        Label all_werewolf_label =new Label("Werewolf",skin);
        table.add(all_werewolf_label);
        table.row();
        
        Label werewolf_label =new Label("Normal",skin);
        table.add(werewolf_label);
        werewolf_textField = new TextField("0",skin);
        werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(werewolf_textField);
        table.row();
        table.row();
        
        
        Label all_citizen_label =new Label("Citizen",skin);
        table.add(all_citizen_label);
        table.row();
        
        Label citizen_label =new Label("Normal",skin);
        table.add(citizen_label);
        citizen_textField = new TextField("0",skin);
        citizen_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(citizen_textField);
        table.row();
        
        Label lover_label =new Label("Lover",skin);
        table.add(lover_label);
        lover_textField = new TextField("0",skin);
        lover_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(lover_textField);
        table.row();
        
        
        Label family_label =new Label("Family",skin);
        table.add(family_label);
        family_textField = new TextField("0",skin);
        family_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(family_textField);
        table.row();
        
        Label medium_label =new Label("Medium",skin);
        table.add(medium_label);
        medium_textField = new TextField("0",skin);
        medium_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(medium_textField);
        table.row();
        
        Label little_girl_label =new Label("Little girl",skin);
        table.add(little_girl_label);
        little_girl_textField = new TextField("0",skin);
        little_girl_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(little_girl_textField);
        table.row();
        
        TextButton creer_button = new TextButton("Créer partie",skin);
        table.add(creer_button);
        table.row();
        
        creer_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        		message.setConversationId("CREATE_GAME_REQUEST");
        		message.setSender(app.agent.getAID());
        		message.addReceiver(DFServices.getSystemController(app.agent));
        		GameSettings gameSettings = 
        				new GameSettings(
        						Integer.parseInt(werewolf_textField.getText()),
        						Integer.parseInt(citizen_textField.getText()),
        						Integer.parseInt(lover_textField.getText()),
        						Integer.parseInt(medium_textField.getText()),
        						Integer.parseInt(little_girl_textField.getText())
        						);
        		ObjectMapper mapper = new ObjectMapper();
        		String json ="";
        		try {
        			json = mapper.writeValueAsString(gameSettings);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		message.setContent(json);
        		app.agent.send(message);
        		
            	
      		
        		app.setScreen(new ViewJoinMenu(app));
            }
        });
        
         stage.addActor(table);
        
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

}
