package org.rabus.ProjectOne.entities;

public class EndFlag extends Entity
{

	public EndFlag(float x, float y)
	{
		this.pos.x = x;
		this.pos.y = y;
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = bounds.height = 1;
	}
}