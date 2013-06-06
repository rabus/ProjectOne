package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Entity
{
	public Vector2 pos = new Vector2();
	public Rectangle bounds = new Rectangle();

	public Entity(Map map, float x, float y)
	{
		this.pos.set(x, y);
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = this.bounds.height = 1;
	}

	public Entity()
	{

	}

	public void dispose()
	{

	}

}
