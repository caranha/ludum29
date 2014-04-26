package org.castelodelego.ld29;

import org.castelodelego.ld29.gameplay.GameplayScreen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LD29Game extends Game {

	public static Screen splashScreen;
	public static Screen mainScreen;
	public static Screen gameplayScreen;
	
	static OrthographicCamera globalcam;

	
	@Override
	public void create() {	
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Globals.init(); // TODO: Send an instance of myself to "globals"
		
		
		// Creating global resource managers
		queueAssets();
		
		splashScreen = new SplashScreen();
		mainScreen = new MainScreen();
		gameplayScreen = new GameplayScreen();

		globalcam = new OrthographicCamera();
		globalcam.setToOrtho(false);	

		
		
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
		
		Globals.log.addMessage("FPS", "FPS: "+Gdx.graphics.getFramesPerSecond());
		Globals.log.render();		
	}

	@Override
	public void resize(int width, int height) {
		globalcam.setToOrtho(false);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
