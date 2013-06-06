package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Monster extends Entity
{
    public static final int IDLE = 0;
    public static final int RUN = 1;
    public static final int JUMP = 2;
    public static final int SPAWN = 3;
    public static final int DYING = 4;
    public static final int DEAD = 999;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final float ACCELERATION = 20f; // Acceleration from idle to running
    public static final float JUMP_VELOCITY = 10; // Jumping velocity
    public static final float GRAVITY = 20.0f; // G-force
    public static final float MAX_VEL = 0.5f; // Maximum running velocity
    public static final float DAMP = 0.90f; // Bounce effect (dampening)

    public static final int SKELETON = 100;
    public static final int ORC = 101;

    AssetManager assets;
    Map map;
    public int type;

    Animation runLeft;
    Animation runRight;
    Animation jumpLeft;
    Animation jumpRight;
    Animation idleLeft;
    Animation idleRight;
    Animation dead;
    Animation spawn;
    Animation dying;
    Animation melting;

    public Vector2 accel = new Vector2();
    public Vector2 vel = new Vector2();

    public int state = SPAWN;
    public float stateTime = 0;
    public int dir = RIGHT;
    boolean grounded = false;
    boolean godmode = false;

    Sound splat;

    public Monster(AssetManager assets, Map map, float x, float y, int type)
    {
        this.type = type;
        this.assets = assets;
        this.map = map;
        this.pos.x = x;
        this.pos.y = y;
        this.bounds.width = 0.6f;
        this.bounds.height = 0.9f;
        this.bounds.x = pos.x + 0.2f;
        this.bounds.y = pos.y;

        splat = assets.get("sfx/splat.wav", Sound.class);
        TextureRegion texture;
        TextureRegion split[];

        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("orc_base"); // Player character sprites

        split = new TextureRegion(texture).split(16, 18)[1]; // Extracts sprites from sheet // Row 1
        idleRight = new Animation(0.5f, split[1]);
        runRight = new Animation(0.1f, split[0], split[2]);
        jumpRight = new Animation(0.1f, split[0]);

        split = new TextureRegion(texture).split(16, 18)[3];
        idleLeft = new Animation(0.5f, split[1]);
        runLeft = new Animation(0.1f, split[0], split[2]);
        jumpLeft = new Animation(0.1f, split[0]);

        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("player");
        split = new TextureRegion(texture).split(20, 20)[1]; // Row 2
        //spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
        dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
        split = new TextureRegion(texture).split(20, 20)[2]; // Row 3
        melting = new Animation(0.1f, split[0], split[1], split[2], split[3], split[3], split[3], split[3], split[3]);
        split = new TextureRegion(texture).split(20, 20)[3]; // Row 4
        spawn = new Animation(0.1f, split[0], split[1], split[2], split[3]);
    }

    public void render(SpriteBatch batch)
    {
        if (this.pos.dst2(map.player.pos) < 140f)
        {
            Animation anim = null;
            float renderSpeed = 1f; // 0.3 for optimal
            boolean loop = true;
            if (this.state == Monster.RUN)
            {
                if (this.dir == Monster.LEFT)
                    anim = runLeft;
                else
                    anim = runRight;
            }
            if (this.state == Monster.IDLE)
            {
                if (this.dir == Monster.LEFT)
                    anim = idleLeft;
                else
                    anim = idleRight;
            }
            if (this.state == Monster.JUMP)
            {
                if (this.dir == Monster.LEFT)
                    anim = jumpLeft;
                else
                    anim = jumpRight;
            }
            if (this.state == Monster.SPAWN)
            {
                anim = spawn;
                loop = false;
                renderSpeed = 1f;
            }
            if (this.state == Monster.DYING)
            {
                anim = dying;
                loop = false;
                renderSpeed = 1f;
            }
            batch.draw(anim.getKeyFrame(this.stateTime * renderSpeed, loop), this.pos.x, this.pos.y, 1, 1);
        }
    }

    public void update(float deltaTime)
    {
        if (state != DYING) // If monsters isn't dying, then move him :)
        {
            processAI();

            accel.y = -GRAVITY;
            accel.mul(deltaTime);
            vel.add(accel.x, accel.y);
            if (accel.x == 0)
                vel.x *= DAMP;
            if (vel.x > MAX_VEL)
                vel.x = MAX_VEL;
            if (vel.x < -MAX_VEL)
                vel.x = -MAX_VEL;
            vel.mul(deltaTime);
            tryMove();
            vel.mul(1.0f / deltaTime);

            if (state == SPAWN)
            {
                if (stateTime > 0.4f) // Time for spawning nimation
                {
                    state = RUN;
                }
            }

            if (map.player.weaponBounds.overlaps(bounds) && map.player.isAttacking) // Check if player's attacks kill monster
            {
                splat.play(map.volume);
                state = DYING;
                stateTime = 0;
            }
            else if (map.player.bounds.overlaps(bounds)) // Check if this monsters touches player and kills him
            {
                if (map.player.state != Player.DYING && state != DYING)
                {
                    map.player.splat.play(map.volume);
                    map.player.state = Player.DYING;
                    map.player.stateTime = 0;
                }
            }
        }
        else // If monster is dying
        {
            if (stateTime > 0.8f) // Time for death animation
                state = DEAD;
        }

        stateTime += deltaTime;
    }

    private void processAI()
    {
        if (state == SPAWN || state == DYING)
            return;

        if (state == IDLE)
            state = RUN;
        else if (state == RUN)
        {
            if (dir == LEFT)
            {
                accel.x = ACCELERATION * dir;
            }
            else if (dir == RIGHT)
            {
                accel.x = ACCELERATION * dir;
            }
        }

        //state = IDLE;
        //accel.x = 0;

        //		if ((Gdx.input.isKeyPressed(Keys.W) || jumpButton) && state != JUMP)
        //		{
        //			//jump.play(1f);
        //			state = JUMP;
        //			vel.y = JUMP_VELOCITY;
        //			grounded = false;
        //		}
    }

    Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    private void tryMove()
    {
        bounds.x += vel.x;
        fetchCollidableRects();
        for (int i = 0; i < r.length; i++)
        {
            Rectangle rect = r[i];
            if (bounds.overlaps(rect))
            {
                if (vel.x < 0)
                    bounds.x = rect.x + rect.width + 0.0001f;
                else
                    bounds.x = rect.x - bounds.width - 0.0001f;
                vel.x = 0;
                dir = -dir; // Reverses direction
            }
        }

        bounds.y += vel.y;
        fetchCollidableRects();
        for (int i = 0; i < r.length; i++)
        {
            Rectangle rect = r[i];
            if (bounds.overlaps(rect))
            {
                if (vel.y < 0)
                {
                    bounds.y = rect.y + rect.height + 0.0001f;
                    grounded = true;
                    if (state != DYING && state != SPAWN)
                        state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
                }
                else
                    bounds.y = rect.y - bounds.height - 0.0001f;
                vel.y = 0;
            }
        }

        pos.x = bounds.x - 0.2f;
        pos.y = bounds.y;
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
        int tile1 = tiles[p1x][p1y];
        int tile2 = tiles[p2x][p2y];
        int tile3 = tiles[p3x][p3y];
        int tile4 = tiles[p4x][p4y];

        if (tile1 == Map.TILE || tile1 == Map.BORDER || tile1 == Map.ROCK || tile1 == Map.MONSTER_BORDER)
            r[0].set(p1x, p1y, 1, 1);
        else
            r[0].set(-1, -1, 0, 0);
        if (tile2 == Map.TILE || tile2 == Map.BORDER || tile2 == Map.ROCK || tile2 == Map.MONSTER_BORDER)
            r[1].set(p2x, p2y, 1, 1);
        else
            r[1].set(-1, -1, 0, 0);
        if (tile3 == Map.TILE || tile3 == Map.BORDER || tile3 == Map.ROCK || tile3 == Map.MONSTER_BORDER)
            r[2].set(p3x, p3y, 1, 1);
        else
            r[2].set(-1, -1, 0, 0);
        if (tile4 == Map.TILE || tile4 == Map.BORDER || tile4 == Map.ROCK || tile4 == Map.MONSTER_BORDER)
            r[3].set(p4x, p4y, 1, 1);
        else
            r[3].set(-1, -1, 0, 0);
    }
}