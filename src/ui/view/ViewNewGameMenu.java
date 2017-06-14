package ui.view;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	
	private TextField exorcist_textField;
	private TextField salvator_textField;
	private TextField scapegoat_textField;
    
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
		table.columnDefaults(6);
		
		 table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	    // table.center();
	       
        
		//table.add(new Label("",skin));
        Label all_werewolf_label =new Label("Werewolf",skin);
        table.add(all_werewolf_label).width(100).left();
        table.add(new Label("",skin));
        table.add(new Label("",skin));
        table.row();
        
        Image image = new Image(new Texture("resources/sprites/werewolf.png"));
        image.setSize(32, 32);
        table.add(image);
        
        Label werewolf_label =new Label("Normal",skin);
        table.add(werewolf_label).width(100).left();
        werewolf_textField = new TextField("0",skin);
        werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(werewolf_textField).left().padRight(50);
        
        
        image = new Image(new Texture("resources/sprites/great_werewolf.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label great_werewolf_label =new Label("Great",skin);
        table.add(great_werewolf_label).width(100).left();
        great_werewolf_textField = new TextField("0",skin);
        great_werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(great_werewolf_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/white_werewolf.png"));
        image.setSize(32, 32);
        table.add(image);
        Label white_werewolf_label =new Label("White",skin);
        table.add(white_werewolf_label).width(100).left();
        white_werewolf_textField = new TextField("0",skin);
        white_werewolf_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(white_werewolf_textField).padRight(50);
        table.row();
        table.row();

      //  table.add(new Label("",skin));
        Label all_citizen_label =new Label("Citizen",skin);
        table.add(all_citizen_label).padTop(20).left();
        table.add(new Label("",skin));
        table.add(new Label("",skin));
        table.row();
        
        image = new Image(new Texture("resources/sprites/citizen.png"));
        image.setSize(32, 32);
        table.add(image);
        Label citizen_label =new Label("Normal",skin);
        table.add(citizen_label).width(100).left();
        citizen_textField = new TextField("0",skin);
        citizen_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(citizen_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/cupid.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label lover_label =new Label("Cupid",skin);
        table.add(lover_label).width(100).left();
        lover_textField = new TextField("0",skin);
        lover_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(lover_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/salvator.png"));
        image.setSize(32, 32);
        table.add(image);
        Label salvator_label =new Label("Salvator",skin);
        table.add(salvator_label).width(100).left();
        salvator_textField = new TextField("0",skin);
        salvator_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(salvator_textField).padRight(50);
        //table.row();
        
        image = new Image(new Texture("resources/sprites/exorcist.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label exorcist_label =new Label("Exorcist",skin);
        table.add(exorcist_label).width(100).left();
        exorcist_textField = new TextField("0",skin);
        exorcist_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(exorcist_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/family.png"));
        image.setSize(32, 32);
        table.add(image);
        Label family_label =new Label("Family",skin);
        table.add(family_label).width(100).left();
        family_textField = new TextField("0",skin);
        family_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(family_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/medium.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label medium_label =new Label("Medium",skin);
        table.add(medium_label).width(100).left();
        medium_textField = new TextField("0",skin);
        medium_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(medium_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/little_girl.png"));
        image.setSize(32, 32);
        table.add(image);
        Label little_girl_label =new Label("Little girl",skin);
        table.add(little_girl_label).width(100).left();
        little_girl_textField = new TextField("0",skin);
        little_girl_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(little_girl_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/hunter.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label hunter_label =new Label("Hunter",skin);
        table.add(hunter_label).width(100).left();
        hunter_textField = new TextField("0",skin);
        hunter_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(hunter_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/angel.png"));
        image.setSize(32, 32);
        table.add(image);
        Label angel_label =new Label("Angel",skin);
        table.add(angel_label).width(100).left();
        angel_textField = new TextField("0",skin);
        angel_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(angel_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/flute_player.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label flute_label =new Label("Flutist",skin);
        table.add(flute_label).width(100).left();
        flute_textField = new TextField("0",skin);
        flute_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(flute_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/ancient.png"));
        image.setSize(32, 32);
        table.add(image);
        Label ancient_label =new Label("Ancient",skin);
        table.add(ancient_label).width(100).left();
        ancient_textField = new TextField("0",skin);
        ancient_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(ancient_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/witch.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label witch_label =new Label("Witch",skin);
        table.add(witch_label).width(100).left();
        witch_textField = new TextField("0",skin);
        witch_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(witch_textField);
        table.row();
        
        image = new Image(new Texture("resources/sprites/thief.png"));
        image.setSize(32, 32);
        table.add(image);
        Label thief_label =new Label("Thief",skin);
        table.add(thief_label).width(100).left();
        thief_textField = new TextField("0",skin);
        thief_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(thief_textField).padRight(50);
        
        image = new Image(new Texture("resources/sprites/scapegoat.png"));
        image.setSize(32, 32);
        table.add(image).padRight(50);
        Label scapegoat_label =new Label("Scapegoat",skin);
        table.add(scapegoat_label).width(100).left();
        scapegoat_textField = new TextField("0",skin);
        scapegoat_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(scapegoat_textField);
        table.row();
        table.row();
        
   
        
     
        
        

        Label humannb_label =new Label("Nombre de joueurs humains : ",skin);
        table.add(humannb_label).padTop(20).padRight(50).left();
        human_textField = new TextField("0",skin);
        human_textField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        table.add(human_textField).padTop(20);
        table.row();
        
        

        final CheckBox box=new CheckBox("Cacher les roles ? ",skin);	 //Added this to fix
        table.add(box).padRight(20).padBottom(10).left();
       // table.row();
        
        TextButton creer_button = new TextButton("Créer partie",skin);
        table.add(creer_button).padRight(20).right();
        
        TextButton retour_button = new TextButton("Retour",skin);
		table.add(retour_button).left();
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
        		players.put(Roles.SALVATOR,Integer.parseInt(salvator_textField.getText())); 
        		players.put(Roles.EXORCIST,Integer.parseInt(exorcist_textField.getText())); 
        		players.put(Roles.SCAPEGOAT,Integer.parseInt(scapegoat_textField.getText())); 
        		
        		
        		
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
