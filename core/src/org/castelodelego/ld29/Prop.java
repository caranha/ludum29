package org.castelodelego.ld29;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Prop implements Poolable {
	Animation anim = null;
	Vector2 pos;
	
	float frametimer = 0;
	float freq = 1;
	float life = 1;
	
	public Prop()
	{
		pos = new Vector2();
	}
	
	/**
	 * Basic animation initializer
	 * @param p - Position for the prop
	 * @param a - Animation for the prop
	 */
	public Prop init(Vector2 p, Animation a)
	{
		anim = a;
		pos.x = p.x;
		pos.y = p.y;
		life = a.animationDuration;
		return this;
	}

	public Prop setfreq(float f)
	{
		freq = f;
		return this;
	}
	
	public Prop setlife(float l)
	{
		life = l;
		return this;
	}

	public Prop setPos(Vector2 p)
	{
		pos.x = p.x;
		pos.y = p.y;
		return this;
	}
	
	public Prop addPos(Vector2 p)
	{
		pos.x += p.x;
		pos.y += p.y;
		return this;
	}
	
	public void update(float dt)
	{
		frametimer += dt*freq;
	}
	
	public boolean isDead()
	{
		return (frametimer > life);		
	}
	
	public void render(Batch b)
	{
		//Gdx.app.debug("Prop rendering", "Animation "+anim+", pos: "+pos);
		b.draw(anim.getKeyFrame(frametimer), pos.x, pos.y);
	}
	
	@Override
	public void reset() {
		frametimer = 0; 
		life = 200;
		pos.x = 0;
		pos.y = 0;
		anim = null;
		freq = 1;
	}
}
