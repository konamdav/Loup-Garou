package ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
 * Hp Bar
 * @author Davy
 *
 */
public class ViewRole {
	private Texture textureRole;
	private int[] position;
	private SpriteBatch batch;
	private ViewRoles roles;

	/**
	 * Constructeur
	 * @param batch moteur graphique
	 * @param type_jauge type base ou type monstre 
	 */
	public ViewRole(ViewRoles roles, String role, int x , int y)
	{
		this.roles = roles;
		this.position=new int[2];
		this.batch=this.roles.getPlayer().getSpriteBatch();
		this.textureRole = null;
		
		if(role.equals("WEREWOLF"))
		{
			this.textureRole = new Texture(Gdx.files.internal("resources/sprites/werewolf.png"));
		}
		else
		{
			this.textureRole = new Texture(Gdx.files.internal("resources/sprites/citizen.png"));
		}
		
		this.position[0] = x;
		this.position[1] = y;
	}

	public void update()
	{
		this.batch.draw(this.textureRole,position[0],position[1]);
	}
}
