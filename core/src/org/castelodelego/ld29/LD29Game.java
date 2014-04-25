package org.castelodelego.ld29;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;

public class LD29Game extends Game {

	static Screen splashScreen;
	static Screen mainScreen;

	
	@Override
	public void create() {	
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Globals.init(); // TODO: Send an instance of myself to "globals"
		
		// Creating global resource managers
		queueAssets();
		
		splashScreen = new SplashScreen();
		mainScreen = new MainScreen();
		
		setScreen(splashScreen);
		
	}

	/**
	 * Add all assets for loading here.
	 * 
	 */
	private void queueAssets()
	{
		//		Globals.manager.load("images-packed/pack.atlas", TextureAtlas.class); // packed images
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void render() {		
		
		super.render();
		
		// Rendering here renders above everything else
		// Good for rendering debug info
		
		// Uncomment for FPS
		Globals.batch.begin();
		Globals.debugtext.setColor(Color.YELLOW);
		Globals.debugtext.draw(Globals.batch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight());		
		Globals.batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
