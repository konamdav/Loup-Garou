package ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Vue de monstre
 * @author Davy
 *
 */
public class ViewPlayer extends ViewEntity{
	private final static String LEFT = "LEFT";
	private final static String RIGHT = "RIGHT";
	private final static String UP = "UP";

	private String direction;
	private int[] position;
	
	private ViewRoles roles;

	private Image imPlayerWake;
	private Image imPlayerSleep;
	private ImageView imgView;
	
	private SpriteAnimation animation;
	private String status;
	private Group group;    

	public int getOffSetXSprite(String s)
	{
		System.out.println(s);
		if(s.equals("UP"))
		{
			return 192;
		}
		else if(s.equals("LEFT"))
		{
			return 64;
		}
		else if(s.equals("RIGHT"))
		{
			System.out.println(s);
			return 128;
		}
		else
		{
			System.out.println("000");
			return 0;
		}
	}
	
	public ViewPlayer(int x, int y, String status, String direction, Group group)
	{
		this.status = status;
		this.group = group;
		
		this.position = new int[2];
		this.position[0] = x * 64;
		this.position[1] = y * 64;
		this.direction = direction;
		
		this.imPlayerWake = new Image("resources/sprites/playerWake.png");
		this.imPlayerSleep = new Image("resources/sprites/playerSleep.png");
		
		
		if(status.equals("WAKE")){
			this.imgView = new ImageView(this.imPlayerWake);
		}
		else
		{
			this.imgView = new ImageView(this.imPlayerSleep);
		}
		
		this.imgView.setX(this.position[0]);
		this.imgView.setY(this.position[1]);
		this.group.getChildren().add(this.imgView);
		
		this.animation = new SpriteAnimation(
                this.imgView,
                Duration.millis(1000),
                2, 1,
                getOffSetXSprite(direction), 0,
                64, 64
        );
		
		
		this.roles = new ViewRoles(this);
	}


	public Group getGroup() {
		return group;
	}

	public void updateWake()
	{
		this.animation.getImageView().setImage(this.imPlayerWake);
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
		
		this.roles.update();
	}
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public void updateSleep()
	{
		this.animation.getImageView().setImage(this.imPlayerSleep);
	}
	

	public ViewRoles getRoles() {
		return roles;
	}


	


	public int[] getPosition() {
		return this.position;
	}
	
	public String getDirection() {
		return this.direction;
	}

}
