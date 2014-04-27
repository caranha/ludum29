package org.castelodelego.ld29;

public class GameContext {

	int maxlevel = 3;
	int currentlevel = 0;
	int lives = 5;
	
	public GameContext(int last)
	{
		maxlevel = last;
	}
	
	public void init()
	{
		lives = 5;
		currentlevel = 0;
	}
	
	public int getLives()
	{
		return lives;
	}
	
	public void addLives(int n)
	{
		lives +=n;
	}
	
	public int getLevel()
	{
		return currentlevel;
	}
	
	public int getmaxlevel()
	{
		return maxlevel;
	}
	
	public void addLevel(int n)
	{
		currentlevel = (currentlevel + n)%maxlevel;
	}
	
}
