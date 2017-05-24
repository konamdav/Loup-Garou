package ui.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ui.view.ViewEntity;

/**
 * Vue de monstre
 * @author Davy
 *
 */
public class ViewPlayer extends ViewEntity{
	private final static String LEFT = "LEFT";
	private final static String RIGHT = "RIGHT";
	private final static String UP = "UP";
	/* Constante de frame */
	private static final int NB_FRAME = 2;         
	private static final int NB_DIRECTION = 4;

	private String direction;
	private int[] position;
	
	private ViewRoles roles;

	/*Le batch qui dessine le sprite*/
	private SpriteBatch spriteBatch;
	
	private Animation animationUpWake; 
	private Animation animationDownWake;
	private Animation animationLeftWake;
	private Animation animationRightWake;
	private Texture sheetWake;             
	private TextureRegion[] framesUpWake;
	private TextureRegion[] framesDownWake;
	private TextureRegion[] framesLeftWake;
	private TextureRegion[] framesRightWake;
	private TextureRegion[][] tmp;
	private TextureRegion couranteFrame;           

	private float stateAnimation;
	private Texture sheetSleep;
	private TextureRegion[] framesUpSleep;
	private Animation animationUpSleep;
	private Animation animationRightSleep;
	private TextureRegion[] framesRightSleep;
	private Animation animationLeftSleep;
	private TextureRegion[] framesLeftSleep;
	private TextureRegion[] framesDownSleep;
	private Animation animationDownSleep;
	
	private Texture sheetDead;
	private TextureRegion[] framesUpDead;
	private Animation animationUpDead;
	private Animation animationRightDead;
	private TextureRegion[] framesRightDead;
	private Animation animationLeftDead;
	private TextureRegion[] framesLeftDead;
	private TextureRegion[] framesDownDead;
	private Animation animationDownDead;
	
	private String status;    


	public ViewPlayer(int x, int y, String status, String direction, SpriteBatch batch)
	{
		this.status = status;
		this.spriteBatch = batch;
		this.position = new int[2];
		this.position[0] = x * 64;
		this.position[1] = y * 64;
		this.direction = direction;
		
		this.roles = new ViewRoles(this);
		

		sheetWake=new Texture(Gdx.files.internal("resources/sprites/PlayerWake.png"));
		/*On découpe la texture */
		tmp = TextureRegion.split(sheetWake, sheetWake.getWidth()/NB_FRAME, sheetWake.getHeight()/NB_DIRECTION);              // #10
		framesUpWake = new TextureRegion[NB_FRAME];
		framesDownWake = new TextureRegion[NB_FRAME];
		framesRightWake = new TextureRegion[NB_FRAME];
		framesLeftWake = new TextureRegion[NB_FRAME];
		stateAnimation = 0;
		
		/*Création des différentes animations*/
		/*animation bas*/
		int index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesDownWake[index++] = tmp[0][i];

		}
		animationDownWake= new Animation(0.25f, framesDownWake); 

		/*animation gauche*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesLeftWake[index++] = tmp[1][i];

		}
		animationLeftWake= new Animation(0.25f, framesLeftWake); 

		/*animation droite*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesRightWake[index++] = tmp[2][i];

		}
		animationRightWake= new Animation(0.25f, framesRightWake); 

		/*animation haut*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesUpWake[index++] = tmp[3][i];

		}
		animationUpWake= new Animation(0.25f, framesUpWake); 

	
		sheetSleep=new Texture(Gdx.files.internal("resources/sprites/PlayerSleep.png"));
		/*On découpe la texture */
		tmp = TextureRegion.split(sheetSleep, sheetSleep.getWidth()/NB_FRAME, sheetSleep.getHeight()/NB_DIRECTION);              // #10
		framesUpSleep = new TextureRegion[NB_FRAME];
		framesDownSleep = new TextureRegion[NB_FRAME];
		framesRightSleep = new TextureRegion[NB_FRAME];
		framesLeftSleep = new TextureRegion[NB_FRAME];
		
		/*Création des différentes animations*/
		/*animation bas*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesDownSleep[index++] = tmp[0][i];

		}
		animationDownSleep= new Animation(0.25f, framesDownSleep); 

		/*animation gauche*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesLeftSleep[index++] = tmp[1][i];

		}
		animationLeftSleep= new Animation(0.25f, framesLeftSleep); 

		/*animation droite*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesRightSleep[index++] = tmp[2][i];

		}
		animationRightSleep= new Animation(0.25f, framesRightSleep); 

		/*animation haut*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesUpSleep[index++] = tmp[3][i];

		}
		animationUpSleep= new Animation(0.25f, framesUpSleep); 
	
		//dead
		
		sheetDead=new Texture(Gdx.files.internal("resources/sprites/PlayerDead.png"));
		/*On découpe la texture */
		tmp = TextureRegion.split(sheetDead, sheetDead.getWidth()/NB_FRAME, sheetDead.getHeight()/NB_DIRECTION);              // #10
		framesUpDead = new TextureRegion[NB_FRAME];
		framesDownDead = new TextureRegion[NB_FRAME];
		framesRightDead = new TextureRegion[NB_FRAME];
		framesLeftDead = new TextureRegion[NB_FRAME];
		
		/*Création des différentes animations*/
		/*animation bas*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesDownDead[index++] = tmp[0][i];

		}
		animationDownDead= new Animation(0.25f, framesDownDead); 

		/*animation gauche*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesLeftDead[index++] = tmp[1][i];

		}
		animationLeftDead= new Animation(0.25f, framesLeftDead); 

		/*animation droite*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesRightDead[index++] = tmp[2][i];

		}
		animationRightDead= new Animation(0.25f, framesRightDead); 

		/*animation haut*/
		index = 0;
		for (int i = 0; i < NB_FRAME; i++) {

			framesUpDead[index++] = tmp[3][i];

		}
		animationUpDead= new Animation(0.25f, framesUpDead); 
		
		
	}


	public void updateWake()
	{
		Animation animation_courante;
		if(direction.equals(LEFT))
		{
			animation_courante=animationLeftWake;
		}
		else if(direction.equals(UP))
		{
			animation_courante=animationUpWake;
		}
		else if(direction.equals(RIGHT))
		{
			animation_courante=animationRightWake;
		}
		else
		{
			animation_courante=animationDownWake;
		}

		this.couranteFrame = animation_courante.getKeyFrame(stateAnimation, true); 
	}
	
	public void update()
	{
		if(this.status.equals("SLEEP"))
		{
			this.updateSleep();
		}
		else if(this.status.equals("WAKE"))
		{
			this.updateWake();
		}
		else if(this.status.equals("DEAD"))
		{
			this.updateDead();
		}
		
		this.stateAnimation += Gdx.graphics.getDeltaTime();
		this.position[0]=position[0];
		this.position[1]=position[1];

		spriteBatch.draw(couranteFrame, this.position[0], this.position[1]);
		this.roles.update();
	}
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		if(!this.status.equals("DEAD"))
		this.status = status;
	}


	public void updateSleep()
	{
		//System.out.println("SLEEP");
		Animation animation_courante;
		if(direction.equals(LEFT))
		{
			animation_courante=animationLeftSleep;
		}
		else if(direction.equals(UP))
		{
			animation_courante=animationUpSleep;
		}
		else if(direction.equals(RIGHT))
		{
			animation_courante=animationRightSleep;
		}
		else
		{
			animation_courante=animationDownSleep;
		}
		
		this.couranteFrame = animation_courante.getKeyFrame(stateAnimation, true); 
	}
	
	public void updateDead()
	{
		//System.out.println("Dead");
		Animation animation_courante;
		if(direction.equals(LEFT))
		{
			animation_courante=animationLeftDead;
		}
		else if(direction.equals(UP))
		{
			animation_courante=animationUpDead;
		}
		else if(direction.equals(RIGHT))
		{
			animation_courante=animationRightDead;
		}
		else
		{
			animation_courante=animationDownDead;
		}
		
		this.couranteFrame = animation_courante.getKeyFrame(stateAnimation, true); 
	}
	

	public ViewRoles getRoles() {
		return roles;
	}


	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}


	public int[] getPosition() {
		return this.position;
	}
	
	public String getDirection() {
		return this.direction;
	}

}
