package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class SimpleEnemy {

	int weight = 1;
	float radius = 10;
	float speed = 75;
	boolean alive = true;
	
	Vector2 pos;
	Vector2 dir;
	
	public SimpleEnemy(float x, float y)
	{
		pos = new Vector2(x,y);
		dir = new Vector2(Globals.dice.nextFloat()-0.5f,Globals.dice.nextFloat()-0.5f).nor();
	}
	
	public void update(float dt)
	{
		pos.x += dir.x*dt*speed;
		pos.y += dir.y*dt*speed;
	}
	
	/**
	 * Indicates that this enemy has collided with the catwalk at this 
	 * vector. Then the enemy has to do the appropriate transformation to 
	 * its speed.
	 * @param disp_s 
	 * 
	 * @param start first point in the colliding segment
	 * @param end second point in the colliding segment
	 */
	public void collide(Vector2 disp, float disp_s)
	{
		dir.mulAdd(disp, 1);
		dir.nor();
	}

	/** 
	 * sets this enemy to be removed in the future, and generate an explosion
	 */
	public void kill()
	{
		// TODO: generate an explosion
		alive = false;
	}

	
	
	/** getters and setters **/
	
	public Vector2 getPos()
	{
		return pos;
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	
	public void render(Batch batch)
	{
		
	}
	
	public void debugRender(ShapeRenderer s)
	{
		s.setColor(Color.BLACK);
		s.circle(pos.x, pos.y, radius);
	}
}
