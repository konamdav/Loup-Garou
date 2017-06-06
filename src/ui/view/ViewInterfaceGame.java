
package ui.view;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
	private Texture textureEndgame;

	Sprite spriteDay = new Sprite(new Texture("resources/sprites/daysprite.png"));
	Sprite spriteNight = new Sprite(new Texture("resources/sprites/nightsprite.png"));
	Sprite spriteCitizenTurn = new Sprite(new Texture("resources/sprites/citizensprite.png"));
	Sprite spriteWerewolfTurn = new Sprite(new Texture("resources/sprites/werewolfsprite.png"));
	Sprite spriteWhitewerewolfTurn = new Sprite(new Texture("resources/sprites/whitewerewolfsprite.png"));
	Sprite spriteGreatwerewolfTurn = new Sprite(new Texture("resources/sprites/greatwerewolfsprite.png"));
	Sprite spriteCupidTurn = new Sprite(new Texture("resources/sprites/cupidsprite.png"));
	Sprite spriteHunterTurn = new Sprite(new Texture("resources/sprites/huntersprite.png"));
	Sprite spriteFamilyTurn = new Sprite(new Texture("resources/sprites/familysprite.png"));
	Sprite spriteWitchTurn = new Sprite(new Texture("resources/sprites/witchsprite.png"));
	Sprite spriteLittlegirlTurn = new Sprite(new Texture("resources/sprites/littlegirlsprite.png"));
	Sprite spriteMediumTurn = new Sprite(new Texture("resources/sprites/mediumsprite.png"));
	Sprite spriteBackgroundLog = new Sprite(new Texture("resources/sprites/logsprite.png"));
	Sprite spriteBackgroundVote = new Sprite(new Texture("resources/sprites/votesprite.png"));
	
	SelectBox<String> selectBox ;
	TextButton okButton;
	TextButton backButton;
	List<String> list_log;
	ScrollPane scrollpane_log;
	List<String> list_vote;
	ScrollPane scrollpane_vote;
	boolean hover;

	private int mapx; 
	private int mapy;
	private App game;
	private Skin skin;

	public ViewInterfaceGame (App game){
		this.ctrlTerrain = new MapController();
		hover = true;
		mapx = 0;
		mapy = 0;
		this.game = game;
		this.game.clean();
	}


	public void show() {
		stage=new Stage(){
			@Override
			public boolean mouseMoved(int x, int y) {
				y=(int) (this.getHeight()-y);

				x = x+10;
				y = y+10;

				mapx = x;
				mapy = y;


				return true;
			}
		};
		skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));

		Table table=new Table();
        table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.top().right();
        
        selectBox = new SelectBox<String>(skin);
        okButton=new TextButton("Vote",skin);
        backButton=new TextButton("Retour",skin);
        table.add(selectBox).width(230).height(60).padRight(5);
        table.add(okButton).width(60).height(60).padRight(5);
        table.add(backButton).width(230).height(60);
        
		Gdx.input.setInputProcessor(stage);
		skin = new Skin( Gdx.files.internal( "resources/visui/uiskin.json" ));
		viewPlayers=new ViewPlayers(((SpriteBatch)stage.getBatch()));

		terrain=new ViewMap((SpriteBatch) stage.getBatch(), ctrlTerrain);

		textureNight = new Texture(Gdx.files.internal("resources/sprites/night.png"));

		textureEndgame = new Texture(Gdx.files.internal("resources/sprites/endgame.png"));

		list_log = new List<String>(skin);
		scrollpane_log = new ScrollPane(list_log);
		scrollpane_log.setFadeScrollBars(false);
		scrollpane_log.setBounds(0,0, 400,200);
		scrollpane_log.setPosition(970,300);


		list_vote = new List<String>(skin);
		scrollpane_vote = new ScrollPane(list_vote);
		scrollpane_vote.setFadeScrollBars(false);
		scrollpane_vote.setBounds(0,0, 400,200);
		scrollpane_vote.setPosition(970,0);

		spriteDay.setX(0);
		spriteDay.setY(510);
		spriteNight.setX(0);
		spriteNight.setY(510);


		spriteCitizenTurn.setX(287);
		spriteCitizenTurn.setY(510);
		spriteWerewolfTurn.setX(287);
		spriteWerewolfTurn.setY(510);
		spriteWhitewerewolfTurn.setX(287);
		spriteWhitewerewolfTurn.setY(510);
		spriteGreatwerewolfTurn.setX(287);
		spriteGreatwerewolfTurn.setY(510);
		spriteCupidTurn.setX(287);
		spriteCupidTurn.setY(510);
		spriteFamilyTurn.setX(287);
		spriteFamilyTurn.setY(510);
		spriteWitchTurn.setX(287);
		spriteWitchTurn.setY(510);
		spriteMediumTurn.setX(287);
		spriteMediumTurn.setY(510);
		spriteHunterTurn.setX(287);
		spriteHunterTurn.setY(510);
		spriteLittlegirlTurn.setX(287);
		spriteLittlegirlTurn.setY(510);
		
		spriteBackgroundLog.setX(960);
		spriteBackgroundLog.setY(280);


		spriteBackgroundVote.setX(960);
		spriteBackgroundVote.setY(0);

		stage.addActor(scrollpane_log);
		stage.addActor(scrollpane_vote);
		
		stage.addActor(table);
		
		backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	game.agent.setFollow(false);
            	game.clean();
				game.setScreen(new ViewJoinMenu(game));
            }
        });

	}


	public void dispose() {
		this.dispose();
		stage.dispose();
	}

	public void render(float arg0)
	{

		//System.gc();
		//Pour actualiser l'interface
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);       
		stage.getBatch().enableBlending();
		stage.getBatch().begin();



		terrain.update();		
		viewPlayers.drawPlayersDead();
		viewPlayers.drawPlayersSleep();




		if(this.game.getGameInformations()!=null){


			if (this.game.getGameInformations().getDayState().equals("NIGHT"))
				spriteNight.draw(stage.getBatch());
			else
				spriteDay.draw(stage.getBatch());

			if (this.game.getGameInformations().getTurn().equals("WEREWOLF"))
				spriteWerewolfTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("GREAT_WEREWOLF"))
				spriteGreatwerewolfTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("WHITE_WEREWOLF"))
				spriteWhitewerewolfTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("HUNTER"))
				spriteHunterTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("FAMILY"))
				spriteFamilyTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("MEDIUM"))
				spriteMediumTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("WITCH"))
				spriteWitchTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("LITTLE_GIRL"))
				spriteLittlegirlTurn.draw(stage.getBatch());
			else if (this.game.getGameInformations().getTurn().equals("CUPID"))
				spriteCupidTurn.draw(stage.getBatch());
			else
				spriteCitizenTurn.draw(stage.getBatch());
			
			spriteBackgroundLog.draw(stage.getBatch());
			spriteBackgroundVote.draw(stage.getBatch());

			if(this.game.getGameInformations().getVote() != null)
			{

				String[] strings = new String[this.game.getGameInformations().getVote().getRequest().getChoices().size()];
				strings = this.game.getGameInformations().getVote().getRequest().getChoices().toArray(strings);
				selectBox.setItems(strings);
			
			}
			
			// Set Iog
			String[] strings = new String[this.game.getGameInformations().getActionLogs().size()];
			strings = this.game.getGameInformations().getActionLogs().toArray(strings);
			list_log.setItems(strings);

			// Set Vote
			if (this.game.getGameInformations().getCurrentResults() != null){
				Map<String, Integer> map_tmp = this.game.getGameInformations().getCurrentResults().getSimpleVoteResults();

				String[] strings_vote = new String[map_tmp.size()];
				int i = 0;
				for(Entry<String, Integer> entry : map_tmp.entrySet())
				{
					String[] parts = entry.getKey().split("@");
					strings_vote[i] = parts[0] + "  : " + entry.getValue();
					i++;
				}

				list_vote.setItems(strings_vote);
			}
			if (!this.game.getGameInformations().isEndGame())
				scrollpane_log.scrollTo(0, 0, 0, 0);
			if (this.game.getGameInformations().isEndGame())
				stage.getBatch().draw(textureEndgame,0,0);
			else if (this.game.getGameInformations().getDayState().equals("NIGHT")) 
				stage.getBatch().draw(textureNight,0,0);
		}



		viewPlayers.drawPlayersWake();

		if(hover)
		{
			BitmapFont  font = new BitmapFont();
			font.setColor(Color.WHITE);
			font.setScale(1.5f);

			String label = viewPlayers.getLabel(mapx, mapy);
			if(!label.isEmpty())
			{
				float width = font.getMultiLineBounds(label).width;
				float height = font.getMultiLineBounds(label).height;

				stage.getBatch().draw(textureNight, mapx-10, mapy-height-10, width+20, height+20);//draw(textureNight,mapx,mapy);
				font.drawMultiLine(stage.getBatch(), label, mapx, mapy);
			}
		}


		if(this.game.getGameInformations()!=null && this.game.getGameInformations().getProfiles()!=null)
			viewPlayers.updatePlayers(this.game.gameInformations.getProfiles());
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