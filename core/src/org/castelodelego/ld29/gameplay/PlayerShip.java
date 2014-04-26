package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
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
	Array<Vector2> cutline;
	
	CatWalk rail;
	
	Vector2 pos;
	
	ShipStates state;
	
	float move_speed = 200;
	float cut_speed = 100;

	Vector2 cut_dir;

	
	public PlayerShip(float x, float y, CatWalk r)
	{
		pos = new Vector2(x,y);
		cut_dir = new Vector2(x,y);
		
		state = ShipStates.MOVING;

		goals = new Array<Vector2>();
		cutline = new Array<Vector2>();

		rail = r;
	}
	
	
	
	public void update(float dt)
	{
	
		switch(state)
		{
		case MOVING:
			moveToGoals(dt);
			break;
		case CUTTING:
			moveToCut(dt);
			break;
		}
	}
	
	public void reset()
	{
		goals.clear();
		cutline.clear();
		state = ShipStates.MOVING;
		setPos(rail.getStartPosition().x, rail.getStartPosition().y);
	}
	

	
	/**
	 * Executes the movement when the player is only moving along the edges. Removes goals from the 
	 * goallist as those are reached.
	 * 
	 * @param dt the time passed since last frame, in order to calculate speed
	 */
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
			pos.x += Math.signum(goals.first().x - pos.x)*(move_speed*dt);
			pos.y = goals.first().y;
		}
		else
		{
			pos.y += Math.signum(goals.first().y - pos.y)*(move_speed*dt);
			pos.x = goals.first().x;
		}
			
		if (goals.first().dst(pos) < 2)
		{
			pos.set(goals.first());
			goals.removeIndex(0);
		}


	}
	
	/**
	 * Moves the ship following the cutting pattern. Checks for collision with 
	 * the cutting line (snake-like) and for reaching the other side of the trail.
	 * 
	 * @param dt
	 */
	void moveToCut(float dt)
	{
		pos.x += cut_dir.x*cut_speed*dt;
		pos.y += cut_dir.y*cut_speed*dt;
		
		// Test if the cut line is cutting the polygon
		
		int cuts = rail.intersectSegmentCatwalk(cutline.get(cutline.size-2), cutline.peek());
		
		if (cuts > 1 || (cutline.size > 2 && cuts > 0))
		{
			pos.set(rail.closestPoint(pos));

			cutline.pop();
			cutline.add(pos.cpy()); // Adding a fixed point in the end

			rail.cutCatwalk(cutline);
			goals.clear();

			state = ShipStates.MOVING;
			return;
		}
		
		// Testing for self-cutting
		if (cutline.size > 4)
		{
			Vector2 start = cutline.get(cutline.size-1);
			Vector2 end = cutline.get(cutline.size-2);
			
			// TODO: exchange "reset" for "DIE"
			for (int i = 0; i < cutline.size-3; i++)
				if (Intersector.intersectSegments(start, end, cutline.get(i), cutline.get(i+1),null))
				{
					reset();
					return;
				}
		}
		
	}
	
	
	/**
	 * Does not modify the "targets" array, just copy its contents
	 * @param targets
	 */
	public void MoveTo(Array<Vector2> targets)
	{
		if (state == ShipStates.MOVING)
			goals.clear();
			goals.addAll(targets);
	}	
	
	public void CutTo(int xdir, int ydir)
	{
		switch(state)
		{
		case MOVING:
			Vector2 test = new Vector2(pos.x+xdir, pos.y+ydir);
			if (OgamMath.isPointInPolygon(test, rail.points)) // Testing that this cut is acceptable in this position
			{
				goals.clear();
				cutline.clear();
				
				state = ShipStates.CUTTING;	
				cut_dir.set(xdir,ydir);
				cutline.add(pos.cpy());
				cutline.add(pos);				
			}
			break;
			
		case CUTTING:
			if (cut_dir.x == xdir || cut_dir.y == ydir)
				return;
			cut_dir.set(xdir,ydir);
			cutline.pop();
			cutline.add(pos.cpy());
			cutline.add(pos);
			break;
		}
		
	}
	
	public void debugRender(ShapeRenderer r)
	{
		if (state == ShipStates.MOVING)
			r.setColor(Color.BLUE);
		else
			r.setColor(Color.RED);
		r.rect(pos.x-5, pos.y-5, 10, 10);

		r.setColor(Color.RED);
		for (int i = 1; i < cutline.size; i++)
			r.line(cutline.get(i-1), cutline.get(i));
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
