package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Painkiller
{
	public Vector2 pos = new Vector2();
	Rectangle bounds = new Rectangle();
	boolean active = false;

	public Painkiller(float x, float y)
	{
		this.pos.x = x;
		this.pos.y = y;
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = bounds.height = 1;
	}
}