package org.castelodelego.ld29;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

/**
 * A hashmap containing all animations in a TextureAtlas, associated to their filenames.
 * Extends Java's HashMap, by adding an initialization method.
 * 
 * TODO: This treats all animations as the same (same time, same animation mode) - I should change 
 * this so that each animation has its time and playmode defined.
 * 
 * @author caranha
 *
 */
public class AnimationManager extends HashMap<String,Animation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8196901426303296905L;

	/**
	 * Loads all the animations from the TextureAtlas into the animation Manager.
	 * Returns true when it finishes loading.
	 * 
	 * FIXME: Derive the length of the animation and the loop type from the filename
	 * @param dt 
	 */
	public boolean loadAnimations(TextureAtlas pack, float dt)
	{
		float time;
		Array<AtlasRegion> a = pack.getRegions();
		for(int i = 0; i < a.size; i++)
			if (a.get(i).index == 0) // is "a" an animation? (-1 if not an animation, other number if middle of the animation
			{
				time = 0.05f;
				this.put(a.get(i).name, new Animation(time,pack.createSprites(a.get(i).name),PlayMode.LOOP));
			}
		return true;
	}	
}
