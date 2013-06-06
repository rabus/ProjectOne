package org.rabus.ProjectOne.entities;

public class Spawner extends Entity
{
	public static final int OFFLINE = 0;
	public static final int ONLINE = 1;
	public static final int DEACTIVATING = 2;
	public static final int ACTIVATING = 3;

	Map map;
	public float stateTime = 0;
	public int state = OFFLINE;
	public boolean active = false;

	public Spawner(Map map, float x, float y)
	{
		this.map = map;
		this.pos.set(x, y);
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = this.bounds.height = 1;
	}
}