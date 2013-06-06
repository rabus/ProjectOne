package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.math.Vector2;

public class Platform extends Entity
{
	static final int FORWARD = 1;
	static final int BACKWARD = -1;
	static final float FORWARD_VEL = 4;
	static final float BACKWARD_VEL = 4;

	int state = FORWARD;

	Map map;
	Vector2 vel = new Vector2();
	//public float angle = 0;
	int fx = 0;
	int fy = 0;
	int bx = 0;
	int by = 0;

	public Platform(Map map, float x, float y)
	{
		this.map = map;
		this.pos.x = x;
		this.pos.y = y;
		//		this.bounds.x = x;
		//		this.bounds.y = y;
		//		this.bounds.width = bounds.height = 1;
		this.bounds.x = pos.x + 0.2f;
		this.bounds.y = pos.y - 0.2f;
		this.bounds.width = 1.0f;
		this.bounds.height = 0.2f;
	}

	public void init()
	{
		int ix = (int) pos.x;
		int iy = (int) pos.y;

		int left = map.tiles[ix - 1][map.tiles[0].length - 1 - iy];
		int right = map.tiles[ix + 1][map.tiles[0].length - 1 - iy];
		int top = map.tiles[ix][map.tiles[0].length - 1 - iy - 1];
		int bottom = map.tiles[ix][map.tiles[0].length - 1 - iy + 1];

		if (left == Map.TILE)
		{
			vel.x = FORWARD_VEL;
			//angle = -90;
			fx = 1;
		}
		if (right == Map.TILE)
		{
			vel.x = -FORWARD_VEL;
			//angle = 90;
			bx = 1;
		}
		if (top == Map.TILE)
		{
			vel.y = -FORWARD_VEL;
			//angle = 180;
			by = -1;
		}
		if (bottom == Map.TILE)
		{
			vel.y = FORWARD_VEL;
			//angle = 0;
			fy = -1;
		}
	}

	public void update(float deltaTime)
	{
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		boolean change = false;
		if (state == FORWARD)
		{
			change = map.tiles[(int) pos.x + fx][map.tiles[0].length - 1 - (int) pos.y + fy] == Map.TILE;
		}
		else
		{
			change = map.tiles[(int) pos.x + bx][map.tiles[0].length - 1 - (int) pos.y + by] == Map.TILE;
		}
		if (change)
		{
			pos.x -= vel.x * deltaTime;
			pos.y -= vel.y * deltaTime;
			state = -state;
			vel.mul(-1);
			if (state == FORWARD)
				vel.nor().mul(FORWARD_VEL);
			if (state == BACKWARD)
				vel.nor().mul(BACKWARD_VEL);
		}

		bounds.x = pos.x;
		bounds.y = pos.y;

		//		if (map.player.bounds.overlaps(bounds))
		//		{
		//			if (map.player.state != Player.DYING)
		//			{
		//				map.player.state = Player.DYING;
		//				map.player.stateTime = 0;
		//			}
		//		}
	}
}
