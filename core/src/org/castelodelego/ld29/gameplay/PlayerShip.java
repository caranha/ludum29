package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Controls the position, movement and state of the player's ship
 * 
 * @author caranha
 *
 */
public class PlayerShip {
	
	static final Animation walk_anim = Globals.animman.get("anim/scissors_stop");
	static final Animation cut_anim = Globals.animman.get("anim/scissors_go_half");
	static final TiledDrawable line_sprite_v = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_red"));
	static final TiledDrawable line_sprite_h = new TiledDrawable(((TextureAtlas) Globals.manager.get("images/pack.atlas", TextureAtlas.class)).findRegion("line_red_h"));
	
	float animtimer = 0;
	int animdir = 0;

	enum ShipStates { SUMMONING, MOVING, CUTTING };
	Array<Vector2> goals;
	Array<Vector2> cutline;
	
	Vector2 pos;
	
	ShipStates state;
	
	float move_speed = 250;
	float cut_speed = 140;
	
	float size = 20;

	Vector2 cut_dir;

	
	public PlayerShip(float x, float y, CatWalk r)
	{		
		pos = new Vector2(x,y);
		cut_dir = new Vector2(x,y);
		
		state = ShipStates.MOVING;

		goals = new Array<Vector2>();
		cutline = new Array<Vector2>();		
	}
	
	
	/**
	 * Calculate the player's movement. Returns True if the player DIED
	 * 
	 * @param dt time since last frame
	 * @param rail the CatWalk
	 * @param enemies array with all enemies that can kill the player
	 * @return
	 */
	public boolean update(float dt, CatWalk rail, Array<SimpleEnemy> enemies)
	{
		animtimer += dt/2;
		
		switch(state)
		{
		case MOVING:
			moveToGoals(dt);
			break;
		case CUTTING:
			if (moveToCut(dt,rail,enemies))
				return true;
			for (int i = 1; i < cutline.size; i++)
				for (SimpleEnemy e: enemies)
					if (Intersector.intersectSegmentCircle(cutline.get(i-1), cutline.get(i), e.getPos(), e.getRadius()*e.getRadius()))
						return true;
			break;
		case SUMMONING:
			break;
		}

		return false;
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
			animdir = (int) (2 - Math.signum(goals.first().x - pos.x));
		}
		else
		{
			pos.y += Math.signum(goals.first().y - pos.y)*(move_speed*dt);
			pos.x = goals.first().x;
			animdir = (int) (1 - Math.signum(goals.first().y - pos.y));
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
	 * @param dt time since last frame
	 * @return true if the player has died
	 */
	boolean moveToCut(float dt, CatWalk rail, Array<SimpleEnemy> enemies)
	{
		pos.x += cut_dir.x*cut_speed*dt;
		pos.y += cut_dir.y*cut_speed*dt;
		
		setAnimDir(cut_dir.x, cut_dir.y);
		
		// Test if the cut line is cutting the polygon
		
		int cuts = rail.intersectSegmentCatwalk(cutline.get(cutline.size-2), cutline.peek());
		
		if (cuts > 1 || (cutline.size > 2 && cuts > 0))
		{
			pos.set(rail.closestPoint(pos));

			cutline.pop();
			cutline.add(pos.cpy()); // Adding a fixed point in the end

			rail.pushCutline(cutline, enemies);
			goals.clear();

			state = ShipStates.MOVING;
			return false;
		}
		
		// Testing for self-cutting
		if (cutline.size > 4)
		{
			Vector2 start = cutline.get(cutline.size-1);
			Vector2 end = cutline.get(cutline.size-2);
			
			for (int i = 0; i < cutline.size-3; i++)
				if (Intersector.intersectSegments(start, end, cutline.get(i), cutline.get(i+1),null))
					return true;
		}		
		return false;		
	}
	
	private void setAnimDir(float x, float y)
	{
		if (Math.signum(y) == 1)
			animdir = 0;
		else if (Math.signum(y) == -1)
			animdir = 2;
		else if (Math.signum(x) == 1)
			animdir = 1;
		else 
			animdir = 3;
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
	
	public void CutTo(int xdir, int ydir, CatWalk rail)
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
		case SUMMONING:
			break;
		}
		
	}
	
	
	public void renderCutline(Batch b)
	{
		// FIXME: When cutting down/right, the line "animates"
		for (int i = 1; i < cutline.size; i++)
			if (Math.abs(cutline.get(i-1).x - cutline.get(i).x) < 0.1f) 
			{  

				float ylow = Math.min(cutline.get(i-1).y, cutline.get(i).y);
				float yhigh = Math.max(cutline.get(i-1).y, cutline.get(i).y);
				float x = cutline.get(i-1).x;
				line_sprite_v.draw(b, x-5, ylow, 10, yhigh-ylow);
			}
			else
			{
				float xlow = Math.min(cutline.get(i-1).x, cutline.get(i).x);
				float xhigh = Math.max(cutline.get(i-1).x, cutline.get(i).x);
				float y = cutline.get(i-1).y;

				line_sprite_h.draw(b, xlow, y-5, xhigh-xlow,10);
			}
	}
	
	public void render(Batch b)
	{
		switch(state)
		{
		case SUMMONING:
			break;
		case MOVING:
			b.draw(walk_anim.getKeyFrame(animtimer), pos.x-20, pos.y-20, 20, 20, 40, 40, 1, 1, animdir*-90);
			break;
		case CUTTING:
			b.draw(cut_anim.getKeyFrame(animtimer), pos.x-20, pos.y-20, 20, 20, 40, 40, 1, 1, animdir*-90);
			break;
		}
	}
	
	public void debugRender(ShapeRenderer r)
	{
		if (state == ShipStates.MOVING)
			r.setColor(Color.BLUE);
		else
			r.setColor(Color.RED);
		r.rect(pos.x-(size/2), pos.y-(size/2), size, size);
	}
	
	public void debugRenderCutline(ShapeRenderer r)
	{
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
