package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Controls the position, movement and state of the player's ship
 * 
 * @author caranha
 *
 */
public class PlayerShip {

	enum ShipStates { MOVING, CUTTING };
	Array<Vector2> goals;
	
	Vector2 pos;
	
	ShipStates state;
	float speed = 100;
	
	public PlayerShip(float x, float y)
	{
		pos = new Vector2(x,y);
		state = ShipStates.MOVING;
		goals = new Array<Vector2>();
	}
	
	
	
	public void update(float dt)
	{
	
		switch(state)
		{
		case MOVING:
			moveToGoals(dt);
			break;
		case CUTTING:
			break;
		}
		

	}

	
	
	// TODO: clamp movement
	void moveToGoals(float dt)
	{
		
		if (goals.size == 0)
		{
			Globals.log.removeMessage("Movement");
			return;
		}
		Globals.log.addMessage("Movement", "Movement from "+pos.x+","+pos.y+" to "+goals.first().x+","+goals.first().y);
		
		if (Math.abs(pos.x - goals.first().x) > 0.01f) // horizontal movement
		{
			pos.x += Math.signum(goals.first().x - pos.x)*(speed*dt);
			pos.y = goals.first().y;
		}
		else
		{
			pos.y += Math.signum(goals.first().y - pos.y)*(speed*dt);
			pos.x = goals.first().x;
		}
			
		if (goals.first().dst(pos) < 1)
		{
			pos.set(goals.first());
			goals.removeIndex(0);
		}


	}
	
	
	
	
	
	public void MoveTo(Array<Vector2> targets)
	{
		if (state == ShipStates.MOVING)
			goals = targets;
	}	
	
	public void debugRender(ShapeRenderer r)
	{
		r.setColor(Color.BLUE);
		r.rect(pos.x-5, pos.y-5, 10, 10);
	}
	

	public void setPos(float x, float y)
	{
		pos.x = x;
		pos.y = y;
	}
	public Vector2 getPos()
	{
		return pos;
	}
	
	
}
