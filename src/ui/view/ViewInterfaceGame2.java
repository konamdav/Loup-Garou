
package ui.view;
//Import des fichiers libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import ui.control.Controleur_Terrain;

public class ViewInterfaceGame2 implements Screen
{
	private Stage stage;
	private ViewTerrain terrain;
	private Controleur_Terrain ctrlTerrain;
	private ViewPlayers graphique_monstres;
	private int iii;
	private Texture textureNight;
	private Table tableLevel2;
	private Skin skin;
	private VerticalGroup v;

	private int i = 0;
	
	public ViewInterfaceGame2 (App game){
		this.ctrlTerrain = new Controleur_Terrain();
	}


	public void show() {
		stage=new Stage();

		skin = new Skin(Gdx.files.internal("resources/visui/uiskin.json"));
		Gdx.input.setInputProcessor(stage);

		 // Initialize
		 v = new VerticalGroup();
		 v.addActor(new Label("First row", skin));
		 v.addActor(new Label("Second Row", skin));
		 v.pack();


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
		scrollPane.setScrollY(20);
		scrollPane.setWidth(555);
		scrollPane.setScrollPercentY(.5f);
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

		 // Add to the 'top'
		 v.addActorAt(0, new Label("I'm at the first row"+(++i), skin));
		 v.pack();
		//tableLevel2.setadd(new Label("THE HOOD6", skin)).row();

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