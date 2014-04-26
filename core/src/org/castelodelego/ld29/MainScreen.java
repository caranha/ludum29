package org.castelodelego.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import org.castelodelego.ld29.gameplay.GameplayScreen;

public class MainScreen implements Screen {

	float timeout = 0;
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		timeout += delta;
		if (timeout > 2)
		{
			((GameplayScreen) LD29Game.gameplayScreen).reset();
			((Game) Gdx.app.getApplicationListener()).setScreen(LD29Game.gameplayScreen);
		}
		
		
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		timeout = 0;
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
