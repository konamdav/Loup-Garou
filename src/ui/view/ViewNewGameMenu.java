package ui.view;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
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
import sma.model.Roles;

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
    TextField human_textField;

    private HashMap<String, TextField> textfields;
	private TextField great_werewolf_textField;
	private TextField white_werewolf_textField;
	private TextField hunter_textField;
	private TextField angel_textField;
	private TextField flute_textField;
	private TextField ancient_textField;
	private TextField thief_textField;
	private TextField witch_textField;
    
	ViewNewGameMenu(App a){
		app = a;
    	//app.newSystemContainer();
		//Gdx.graphics.setDisplayMode(800, 500, false);
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));
        textfields = new HashMap<String, TextField>();
        
        
		Table table=new Table();
//        table.setSize(800,500);
  
		 table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	       table.center();
	       
        
        
        Label all_werewolf_label =new Label("Werewolf",skin);
        table.add(all_werewolf_label);
        table.row();
        
        Label werewolf_label =new Label("Normal",skin);
        table.add(werewolf_label);
        werewolf_textField = new TextField("0",skin);
        werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(werewolf_textField);
        
        Label great_werewolf_label =new Label("Great",skin);
        table.add(great_werewolf_label);
        great_werewolf_textField = new TextField("0",skin);
        great_werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(great_werewolf_textField);
        table.row();
        
        
        Label white_werewolf_label =new Label("White",skin);
        table.add(white_werewolf_label);
        white_werewolf_textField = new TextField("0",skin);
        white_werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(white_werewolf_textField);
        table.row();
        table.row();

        
        Label all_citizen_label =new Label("Citizen",skin);
        table.add(all_citizen_label).padTop(20);
        table.row();
        
        Label citizen_label =new Label("Normal",skin);
        table.add(citizen_label);
        citizen_textField = new TextField("0",skin);
        citizen_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(citizen_textField);
        
        Label lover_label =new Label("Cupid",skin);
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
        
        Label hunter_label =new Label("Hunter",skin);
        table.add(hunter_label);
        hunter_textField = new TextField("0",skin);
        hunter_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(hunter_textField);
        table.row();
        
        Label angel_label =new Label("Angel",skin);
        table.add(angel_label);
        angel_textField = new TextField("0",skin);
        angel_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(angel_textField);
        
        Label flute_label =new Label("Flutist",skin);
        table.add(flute_label);
        flute_textField = new TextField("0",skin);
        flute_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(flute_textField);
        table.row();
        
        Label ancient_label =new Label("Ancient",skin);
        table.add(ancient_label);
        ancient_textField = new TextField("0",skin);
        ancient_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(ancient_textField);
        
        
        Label witch_label =new Label("Witch",skin);
        table.add(witch_label);
        witch_textField = new TextField("0",skin);
        witch_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(witch_textField);
        table.row();
        
        Label thief_label =new Label("Thief",skin);
        table.add(thief_label);
        thief_textField = new TextField("0",skin);
        thief_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(thief_textField);
        table.row();
        

        Label human_label =new Label("Joueurs humains",skin);
        table.add(human_label).padTop(20);
        table.row();        

        Label humannb_label =new Label("Nombre : ",skin);
        table.add(humannb_label);
        human_textField = new TextField("0",skin);
        human_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(human_textField);
        table.row();
        
        

        final CheckBox box=new CheckBox("  Cacher les roles",skin);	 //Added this to fix
        table.add(box).padTop(20);
        table.row();
        
        TextButton creer_button = new TextButton("Cr�er partie",skin);
        table.add(creer_button).padTop(20);
        table.row();
        
        TextButton retour_button = new TextButton("Retour",skin);
		table.add(retour_button);
		table.row();

        
        creer_button.addListener(new ClickListener(){
			@Override
            public void clicked(InputEvent event, float x, float y) {
				
			    HashMap<String, Integer> players = new HashMap<String, Integer>();
            	ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        		message.setConversationId("CREATE_GAME_REQUEST");
        		message.setSender(app.agent.getAID());
        		message.addReceiver(DFServices.getSystemController(app.agent));

        		players.put(Roles.WEREWOLF, Integer.parseInt(werewolf_textField.getText()));
        		players.put(Roles.WHITE_WEREWOLF,Integer.parseInt(white_werewolf_textField.getText())); 
        		players.put(Roles.GREAT_WEREWOLF,Integer.parseInt(great_werewolf_textField.getText())); 
        		players.put(Roles.HUNTER, Integer.parseInt(hunter_textField.getText()));
        		players.put(Roles.FAMILY, Integer.parseInt(family_textField.getText()));
        		players.put(Roles.CITIZEN, Integer.parseInt(citizen_textField.getText()));
        		players.put(Roles.CUPID, Integer.parseInt(lover_textField.getText()));
        		players.put(Roles.LITTLE_GIRL, Integer.parseInt(little_girl_textField.getText()));
        		players.put(Roles.MEDIUM,Integer.parseInt(medium_textField.getText()));
        		players.put(Roles.ANGEL,Integer.parseInt(angel_textField.getText()));
        		players.put(Roles.FLUTE_PLAYER,Integer.parseInt(flute_textField.getText()));
        		players.put(Roles.ANCIENT,Integer.parseInt(ancient_textField.getText())); 
        		players.put(Roles.WITCH,Integer.parseInt(witch_textField.getText()));
        		players.put(Roles.THIEF,Integer.parseInt(thief_textField.getText())); 
        		
        		
        		
        		GameSettings gameSettings = new GameSettings(players, Integer.parseInt(human_textField.getText()), box.isChecked());
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

		retour_button.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
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
