package org.castelodelego.ld29.gameplay;

import org.castelodelego.ld29.Globals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameTouchListener implements GestureListener {
	Camera gamecam;
	GameplayScreen parent;
	
	Vector3 rawcoords;
	Vector3 processedcoords;
	
	public GameTouchListener(Camera cam, GameplayScreen p)
	{
		gamecam = cam;
		parent = p;
		
		rawcoords = new Vector3();
		processedcoords = new Vector3();
	}
	
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		processedcoords = gamecam.unproject(rawcoords.set(x, y, 0));
		Globals.log.addMessage("command", "Last Command: Tap "+processedcoords.x+","+processedcoords.y);
		parent.sendTouch(processedcoords.x,processedcoords.y);
		return true;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		int moveX = 0;
		int moveY = 0;
		
		if (Math.abs(velocityX) > Math.abs(velocityY))
			moveX = (int) Math.signum(velocityX);
		else
			moveY = (int) (-1*Math.signum(velocityY));
		
		Globals.log.addMessage("command", "Last Command: Fling "+moveX+","+moveY+" --- ("+velocityX+","+velocityY+")");
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
