package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Entity
{
    public static final int ENERGY_BALL = 10;
    public static final int ROCKET = 11;
    public static final int ARROW = 12;

    public static final int FLYING = 0;
    public static final int EXPLODING = 1;
    public static final int DEAD = 2;
    public static final float VELOCITY = 6;

    AssetManager assets;
    Map map;
    Animation flying = null;
    Animation explosion = null;
    Float angle = 0f;
    TextureRegion launcher;

    public float stateTime = 0;
    public int state = FLYING;
    public Vector2 startPos = new Vector2();
    public Vector2 vel = new Vector2();
    public int type = 10;

    Sound explode, launch;

    public Projectile(AssetManager assets, Map map, float x, float y, int type)
    {
        this.assets = assets;
        this.map = map;
        this.startPos.set(x, y);
        this.pos.set(x, y);
        this.bounds.x = x + 0.2f;
        this.bounds.y = y + 0.2f;
        this.bounds.width = 0.6f;
        this.bounds.height = 0.6f;
        this.vel.set(-VELOCITY, 0);
        this.type = type;

        //launch = Gdx.audio.newSound(Gdx.files.internal("sfx/iceball.wav"));
        explode = assets.get("sfx/explode.wav", Sound.class);

        TextureRegion region = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("objects20x20");// Normal (20x20) objects sprites
        TextureRegion[] texture = new TextureRegion(region).split(20, 20)[0]; // Row 1
        launcher = texture[4];
        if (type == Projectile.ROCKET)
        {
            flying = new Animation(0.1f, texture[2], texture[3]);
            texture = new TextureRegion(region).split(20, 20)[2]; // Row 3
            explosion = new Animation(0.1f, texture[0], texture[1], texture[2], texture[3], texture[4], texture[5]);
        }
        else if (type == Projectile.ENERGY_BALL)
        {
            flying = new Animation(0.1f, texture[0], texture[1]);
            texture = new TextureRegion(region).split(20, 20)[1]; // Row 2
            explosion = new Animation(0.1f, texture[0], texture[1], texture[2], texture[3], texture[4], texture[5]);
        }
    }

    public void render(SpriteBatch batch)
    {
        if (this.state == Projectile.FLYING)
            batch.draw(this.flying.getKeyFrame(this.stateTime, true), this.pos.x, this.pos.y, 0.5f, 0.5f, 1, 1, 1, 1, this.vel.angle());
        else
            batch.draw(this.explosion.getKeyFrame(this.stateTime, false), this.pos.x, this.pos.y, 1, 1);
        batch.draw(launcher, this.startPos.x, this.startPos.y, 1, 1);
    }


    public void update(float deltaTime)
    {
        if (state == FLYING)
        {
            // if(pos.dst(map.bob.pos) < pos.dst(map.cube.pos)) vel.set(map.bob.pos);
            // else vel.set(map.cube.pos);
            if (this.pos.dst(map.player.pos) < 6f) // Flies if player near (6 units away)
                vel.set(map.player.pos);
            else
                vel.set(startPos);

            vel.sub(pos).nor().mul(VELOCITY);
            pos.add(vel.x * deltaTime, vel.y * deltaTime);
            bounds.x = pos.x + 0.2f;
            bounds.y = pos.y + 0.2f;
            if (checkHit())
            {
                state = EXPLODING;
                if (this.pos.dst(map.player.pos) < 4f)
                    explode.play(0.5f);
                stateTime = 0;
            }
        }

        if (state == EXPLODING)
        {
            if (stateTime > 0.6f)
            {
                //if (this.pos.dst2(map.player.pos) < 400f)
                //	launch.play(1f);
                state = FLYING;
                stateTime = 0;
                pos.set(startPos);
                bounds.x = pos.x + 0.2f;
                bounds.y = pos.y + 0.2f;
            }
        }

        stateTime += deltaTime;
    }

    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    private boolean checkHit()
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
            if (map.player.state != Player.DYING)
            {
                map.player.splat.play(0.5f);
                map.player.state = Player.DYING;
                map.player.stateTime = 0;
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
        //launch.dispose();
        //explode.dispose();
    }
}
