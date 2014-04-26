package org.castelodelego.ld29;

import java.util.Random;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Contains all the statically accessible global variables.
 * 
 * @author caranha
 *
 */
public class Globals {
	
	public static InputMultiplexer multiplexer;
	
	public static Preferences scoreloader;
	public static LogOverlay log;
	
	public static AssetManager manager;
	public static AnimationManager animman;
	public static SpriteBatch batch;
	public static Random dice;
		
	public static BitmapFont debugtext;
	
	
	static void init()
	{
		debugtext = new BitmapFont();
		
		batch = new SpriteBatch();
		animman = new AnimationManager();
		manager = new AssetManager();
		
		dice = new Random();
		log = new LogOverlay();
		
		multiplexer = new InputMultiplexer();
	}		
}
