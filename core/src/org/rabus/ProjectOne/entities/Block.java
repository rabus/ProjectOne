package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Block extends Entity
{
	// Standard Block type chart depending on placement:
	//
	// 1 2 3 - top_left, top_center, top_right
	// 4 5 6 - middle_left, middle_center, middle_right
	// 7 8 9 - bottom_left, bottom_center, bottom_right
	//
	// Additional Block types:
	//
	// 10 11 12 - horizontal_left, horizontal_center, horizontal_right
	//
	//    13 - vertical_top
	//    14 - vertical_middle
	//    15 - vertical_bottom
	//
	//    16 - alone block (1x1)

	public static final int TOP_LEFT = 1;
	public static final int TOP_CENTER = 2;
	public static final int TOP_RIGHT = 3;
	public static final int MIDDLE_LEFT = 4;
	public static final int MIDDLE_CENTER = 5;
	public static final int MIDDLE_RIGHT = 6;
	public static final int BOTTOM_LEFT = 7;
	public static final int BOTTOM_CENTER = 8;
	public static final int BOTTOM_RIGHT = 9;

	public static final int HORIZONTAL_LEFT = 10;
	public static final int HORIZONTAL_CENTER = 11;
	public static final int HORIZONTAL_RIGHT = 12;

	public static final int VERTICAL_TOP = 13;
	public static final int VERTICAL_MIDDLE = 14;
	public static final int VERTICAL_BOTTOM = 15;

	public static final int ALONE = 16;

	AssetManager assets;
	Map map;

	public float stateTime = 0;
	public int type;
	public TextureRegion skin;

	public Block(AssetManager assets, Map map, float x, float y, int type)
	{
		this.assets = assets;
		this.map = map;
		this.pos.set(x, y);
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = 1.0f;
		this.bounds.height = 1.0f;
		this.type = type;

		switch (this.type)
		{
			case Block.TOP_LEFT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][3]; // TOP_LEFT [0][3]
				break;
			case Block.TOP_CENTER:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[2][0 + (int) (Math.random() * ((7 - 0) + 1))]; // TOP_CENTER [2][0-7] or [0][0]
				break;
			case Block.TOP_RIGHT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][5]; // TOP_RIGHT [0][5]
				break;
			case Block.MIDDLE_LEFT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][5]; // MIDDLE_LEFT [1][5]
				break;
			case Block.MIDDLE_CENTER:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[3][0 + (int) (Math.random() * ((7 - 0) + 1))]; // MIDDLE_CENTER [3][0-7] or [1,7]
				break;
			case Block.MIDDLE_RIGHT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][6]; // MIDDLE_RIGHT [1][6]
				break;
			case Block.BOTTOM_LEFT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][2]; // BOTTOM_LEFT [1][2]
				break;
			case Block.BOTTOM_CENTER:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][0]; // BOTTOM_CENTER [1][0]
				break;
			case Block.BOTTOM_RIGHT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][3]; // BOTTOM_RIGHT [1][3]
				break;
			case Block.HORIZONTAL_LEFT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][4]; // HORIZONTAL_LEFT [0][4]
				break;
			case Block.HORIZONTAL_CENTER:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][7]; // HORIZONTAL_CENTER [0][7]
				break;
			case Block.HORIZONTAL_RIGHT:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][6]; // HORIZONTAL_RIGHT [0][6]
				break;
			case Block.VERTICAL_TOP:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][1]; // VERTICAL_TOP [0][1]
				break;
			case Block.VERTICAL_MIDDLE:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][4]; // VERTICAL_MIDDLE [1][4]
				break;
			case Block.VERTICAL_BOTTOM:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[1][1]; // VERTICAL_BOTTOM [1][1]
				break;
			case Block.ALONE:
				this.skin = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("tiles").split(20, 20)[0][2]; // ALONE [0][2]
				break;
			default:
				// Default behaviour (invalid block type?)
				break;
		}
	}

	public void update(float deltaTime)
	{
		if (this.pos.dst2(map.player.pos) < 10f)
			checkCollisions();

		stateTime += deltaTime;
	}

	public void render(SpriteBatch batch)
	{
		if (this.pos.dst2(map.player.pos) < 140f)
			batch.draw(skin, pos.x, pos.y, 1, 1);
	}

	Rectangle[] r = { new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle() };

	private boolean checkCollisions()
	{
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++)
		{
			if (bounds.overlaps(r[i]))
			{
				//return true; // Fluids don't have to interact with other collides
			}
		}

		if (map.player.bounds.overlaps(bounds))
		{
			//			if (map.player.state != Player.DYING)
			//			{
			//				map.player.splat.play(1f);
			//				map.player.state = Player.DYING;
			//				map.player.stateTime = 0;
			//			}
			return true;
		}

		return false;
	}

	private void fetchCollidableRects()
	{
		int p1x = (int) bounds.x;
		int p1y = (int) Math.floor(bounds.y);
		int p2x = (int) (bounds.x + bounds.width);
		int p2y = (int) Math.floor(bounds.y);
		int p3x = (int) (bounds.x + bounds.width);
		int p3y = (int) (bounds.y + bounds.height);
		int p4x = (int) bounds.x;
		int p4y = (int) (bounds.y + bounds.height);

		int[][] tiles = map.tiles;
		int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
		int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
		int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
		int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];

		if (tile1 != Map.EMPTY)
			r[0].set(p1x, p1y, 1, 1);
		else
			r[0].set(-1, -1, 0, 0);
		if (tile2 != Map.EMPTY)
			r[1].set(p2x, p2y, 1, 1);
		else
			r[1].set(-1, -1, 0, 0);
		if (tile3 != Map.EMPTY)
			r[2].set(p3x, p3y, 1, 1);
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 != Map.EMPTY)
			r[3].set(p4x, p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);
	}

	public void dispose()
	{
		//pickUp.dispose();
	}
}
