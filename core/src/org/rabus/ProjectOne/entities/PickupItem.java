package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PickupItem extends Entity
{
    public static final int ARMOR_PEASANT = 0;
    public static final int ARMOR_WARRIOR = 1;
    public static final int ARMOR_MAGE = 2;

    AssetManager assets;
    Map map;

    public float stateTime = 0;
    public boolean collected = false;
    public int type;
    Animation animation;
    TextureRegion texture;
    TextureRegion[] region;

    Sound pickUp;

    public PickupItem(AssetManager assets, Map map, float x, float y, int type)
    {
        this.assets = assets;
        this.map = map;
        this.pos.set(x, y);
        this.bounds.x = x;
        this.bounds.y = y;
        this.bounds.width = this.bounds.height = 0.8f;
        this.type = type;
        this.region = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("objects20x20").split(20, 20)[4];
        this.texture = region[5];
        //this.animation = new Animation(0.9f, region[4], region[5]);
//        if (this.type == PickupItem.PEASANT)
//            this.animation = new Animation(0.9f, region[0], region[1]);
//        else if (this.type == PickupItem.WARRIOR)
//            this.animation = new Animation(0.9f, region[2], region[3]);
//        else if (this.type == PickupItem.MAGE)
//            this.animation = new Animation(0.9f, region[4], region[5]);

        pickUp = assets.get("sfx/pickup_crystal.wav", Sound.class);
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
            //batch.draw(this.animation.getKeyFrame(this.stateTime, true), pos.x, pos.y, 1, 1);
            batch.draw(texture, pos.x, pos.y, 1, 1);
    }

    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    private boolean checkCollisions()
    {
        fetchCollidableRects();
        for (int i = 0; i < r.length; i++)
        {
            if (bounds.overlaps(r[i]))
            {
                return true;
            }
        }

        if (map.player.bounds.overlaps(bounds))
        {
            if (!collected)
            {
                pickUp.play(map.volume);
                map.player.changeArmor(this.type);
                collected = true;
            }
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

    }
}
