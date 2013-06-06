package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.newdawn.slick.util.pathfinding.Mover;

public class Player extends Entity
{
    public static final int IDLE = 0;
    public static final int RUN = 1;
    public static final int JUMP = 2;
    public static final int SPAWN = 3;
    public static final int DYING = 4;
    public static final int ATTACK = 10;
    public static final int DEAD = 999;

    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int TOP = 1;
    public static final int BOTTOM = -1;

    public static final float ACCELERATION = 20f; // Acceleration from idle to running
    public static final float JUMP_VELOCITY = 10; // Jumping velocity
    public static final float GRAVITY = 20.0f; // G-force
    public static final float MAX_VEL = 6f; // Maximum running velocity
    public static final float DAMP = 0.70f; // Bounce effect (dampening)

    /* ARMOR TYPES */
    public static final int ARMOR_PEASANT = 0;
    public static final int ARMOR_WARRIOR = 1;

    AssetManager assets;
    Map map;

    Animation runRight;
    Animation runLeft;
    Animation jumpRight;
    Animation jumpLeft;
    Animation idleRight;
    Animation idleLeft;
    Animation attackRight;
    Animation attackLeft;

    Animation empty;
    Animation spawn;
    Animation dying;
    Animation melting;

    Animation armorRunRight;
    Animation armorRunLeft;
    Animation armorJumpRight;
    Animation armorJumpLeft;
    Animation armorIdleRight;
    Animation armorIdleLeft;
    Animation armorAttackRight;
    Animation armorAttackLeft;

    Animation weaponRight;
    Animation weaponLeft;

    Animation hairAttackRight;
    Animation hairAttackLeft;
    Animation hairRight;
    Animation hairLeft;

    public Vector2 accel = new Vector2();
    public Vector2 vel = new Vector2();

    public int state = SPAWN;
    public float stateTime = 0;
    public int dir = RIGHT;
    public int vDir = TOP;
    public boolean grounded = false;
    public boolean isAttacking = false;
    boolean godmode = false;

    public Rectangle weaponBounds = new Rectangle();

    public String name;
    public int id;

    public int crystals = 0;
    public int score = 0;
    public int health = 3;
    Sound splat;

    public Player(AssetManager assets, Map map, float x, float y)
    {
        this.assets = assets;
        this.map = map;
        this.pos.x = x;
        this.pos.y = y;
        this.bounds.width = 0.6f; // Width of player
        this.bounds.height = 0.85f; // Height of player
        this.bounds.x = pos.x + 0.2f;
        this.bounds.y = pos.y;
        this.state = SPAWN;
        this.stateTime = 0;
        this.name = "Rabus";

        // Weapon bounds according to player position and direction
        this.weaponBounds.x = this.pos.x + dir - (0.2f * dir);
        this.weaponBounds.y = this.pos.y + 0.25f;
        this.weaponBounds.width = 0.8f;
        this.weaponBounds.height = 0.2f;

        //jump = Gdx.audio.newSound(Gdx.files.internal("sfx/items/jump.wav"));
        splat = assets.get("sfx/splat.wav", Sound.class);
        TextureRegion texture;
        TextureRegion split[];

        /* EMPTY */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("empty");
        split = new TextureRegion(texture).split(20, 20)[0];
        empty = new Animation(0.1f, split[0]);

        /* MOVEMENT */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("human_base"); // Player character sprites
        // Right
        split = new TextureRegion(texture).split(16, 18)[1];
        idleRight = new Animation(0.5f, split[1]);
        runRight = new Animation(0.1f, split[0], split[2]);
        jumpRight = new Animation(0.1f, split[0]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[3];
        idleLeft = new Animation(0.5f, split[1]);
        runLeft = new Animation(0.1f, split[0], split[2]);
        jumpLeft = new Animation(0.1f, split[0]);

        /* ATTACK */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("human_attack"); // Player character sprites
        // Right
        split = new TextureRegion(texture).split(16, 18)[1];
        attackRight = new Animation(0.1f, split[0], split[1], split[2], split[3]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[3];
        attackLeft = new Animation(0.1f, split[0], split[1], split[2], split[3]);

        /* HAIR */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("human_hair");
        // Right
        split = new TextureRegion(texture).split(16, 18)[1];
        hairRight = new Animation(0.1f, split[0]);
        hairAttackRight = new Animation(0.1f, split[0], split[1], split[2], split[3]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[3];
        hairLeft = new Animation(0.1f, split[0]);
        hairAttackLeft = new Animation(0.1f, split[0], split[1], split[2], split[3]);

        /* WEAPON */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("weapons");
        // Right
        split = new TextureRegion(texture).split(16, 18)[0];
        weaponRight = new Animation(0.1f, split[0], split[1], split[2], split[3]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[1];
        weaponLeft = new Animation(0.1f, split[0], split[1], split[2], split[3]);

        /* ARMOR */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("armor_peasant");
        // Right
        split = new TextureRegion(texture).split(16, 18)[1]; // Extracts sprites from sheet // Row 1
        armorIdleRight = new Animation(0.5f, split[1]);
        armorRunRight = new Animation(0.1f, split[0], split[2]);
        armorJumpRight = new Animation(0.1f, split[0]);
        armorAttackRight = new Animation(0.1f, split[3], split[4], split[5], split[6]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[3];
        armorIdleLeft = new Animation(0.5f, split[1]);
        armorRunLeft = new Animation(0.1f, split[0], split[2]);
        armorJumpLeft = new Animation(0.1f, split[0]);
        armorAttackLeft = new Animation(0.1f, split[3], split[4], split[5], split[6]);

        /* OTHER (to be redone) */
        texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("player");
        split = new TextureRegion(texture).split(20, 20)[1]; // Row 2
        //spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
        dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
        split = new TextureRegion(texture).split(20, 20)[2]; // Row 3
        melting = new Animation(0.1f, split[0], split[1], split[2], split[3], split[3], split[3], split[3], split[3]);
        split = new TextureRegion(texture).split(20, 20)[3]; // Row 4
        spawn = new Animation(0.1f, split[0], split[1], split[2], split[3]);
    }

    public Player()
    {
        this.health = 3;
        this.score = 0;
        this.crystals = 0;
    }

    public void changeArmor(int armorType)
    {
        TextureRegion texture = null;
        switch (armorType)
        {
            case ARMOR_PEASANT:
                texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("armor_peasant");
                break;
            case ARMOR_WARRIOR:
                texture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("armor_warrior");
                break;
            default:
                break;
        }
        TextureRegion[] split = new TextureRegion(texture).split(16, 18)[1];

        // Right
        armorIdleRight = new Animation(0.5f, split[1]);
        armorRunRight = new Animation(0.1f, split[0], split[2]);
        armorJumpRight = new Animation(0.1f, split[0]);
        armorAttackRight = new Animation(0.1f, split[3], split[4], split[5], split[6]);
        // Left
        split = new TextureRegion(texture).split(16, 18)[3];
        armorIdleLeft = new Animation(0.5f, split[1]);
        armorRunLeft = new Animation(0.1f, split[0], split[2]);
        armorJumpLeft = new Animation(0.1f, split[0]);
        armorAttackLeft = new Animation(0.1f, split[3], split[4], split[5], split[6]);
    }

    public void render(SpriteBatch batch)
    {
        Animation body = null;
        Animation armor = null;
        Animation weapon = null;
        Animation hair = null;
        boolean loop = true;

        if (this.state == Player.RUN)
        {
            if (this.dir == Player.LEFT)
            {
                body = runLeft;
                //hair = hairLeft;
                armor = armorRunLeft;
            }
            else
            {
                body = runRight;
                //hair = hairRight;
                armor = armorRunRight;
            }
        }
        if (this.state == Player.IDLE)
        {
            if (this.dir == Player.LEFT)
            {
                body = idleLeft;
                armor = armorIdleLeft;
                //hair = hairLeft;
            }
            else
            {
                body = idleRight;
                //hair = hairRight;
                armor = armorIdleRight;
            }
        }
        if (this.state == Player.JUMP)
        {
            if (this.dir == Player.LEFT)
            {
                body = jumpLeft;
                armor = armorJumpLeft;
                //hair = hairLeft;
            }
            else
            {
                body = jumpRight;
                //hair = hairRight;
                armor = armorJumpRight;
            }
        }

        if (isAttacking)
        {
            if (this.dir == Player.LEFT)
            {
                body = attackLeft;
                armor = armorAttackLeft;
                weapon = weaponLeft;
                hair = hairAttackLeft;
            }
            else
            {
                body = attackRight;
                armor = armorAttackRight;
                weapon = weaponRight;
                hair = hairAttackRight;
            }
            if (state == DYING)
                weapon = empty;
            batch.draw(weapon.getKeyFrame(this.stateTime, loop), this.pos.x + dir - (0.2f * dir), this.pos.y, 1, 1); // Renders weapon
        }
        else // Enable hair for movement
        {
            if (this.dir == Player.LEFT)
                hair = hairLeft;
            else
                hair = hairRight;
        }

        if (this.state == Player.SPAWN)
        {
            body = spawn;
            hair = empty;
            armor = empty;
            loop = false;
        }
        if (this.state == Player.DYING)
        {
            body = dying;
            hair = empty;
            armor = empty;
            loop = false;
        }

        batch.draw(body.getKeyFrame(this.stateTime, loop), this.pos.x, this.pos.y, 1, 1); // Renders body
        batch.draw(armor.getKeyFrame(this.stateTime, loop), this.pos.x, this.pos.y, 1, 1); // Renders armor
        batch.draw(hair.getKeyFrame(this.stateTime, loop), this.pos.x, this.pos.y, 1, 1); // Renders hair
    }

    public void update(float deltaTime)
    {
        if (state != DYING)
        {
            // If crystal count hits 100, add one health
            if (this.crystals >= 100)
            {
                this.health += 1;
                this.crystals -= 100;
            }

            // Checks key pressed (or screen touched)
            //processKeys();

            accel.y = -GRAVITY;
            accel.mul(deltaTime);
            vel.add(accel.x, accel.y);
            if (accel.x == 0)
                vel.x *= DAMP; // Can add dampening effect on different ground types (ice, mud)
            if (vel.x > MAX_VEL)
                vel.x = MAX_VEL;
            if (vel.x < -MAX_VEL)
                vel.x = -MAX_VEL;
            vel.mul(deltaTime);
            tryMove();
            vel.mul(1.0f / deltaTime);

            // Adjust weapon collision box according to player movement
            weaponBounds.x = pos.x + dir - (0.2f * dir);
            weaponBounds.y = pos.y + 0.25f;

            if (state == SPAWN)
            {
                if (stateTime > 0.4f) // Spawn time for animation
                {
                    state = IDLE;
                }
            }
        }
        else // If player is dying
        {
            if (stateTime > 0.8f) // Time for death animation
                state = DEAD;
        }

        stateTime += deltaTime;
    }

    private void processKeys()
    {
        if (state == SPAWN || state == DYING)
            return;

        float x0 = (Gdx.input.getX(0) / (float) Gdx.graphics.getWidth()) * 800;
        float x1 = (Gdx.input.getX(1) / (float) Gdx.graphics.getWidth()) * 800;
        float y0 = 480 - (Gdx.input.getY(0) / (float) Gdx.graphics.getHeight()) * 480;

        boolean leftButton = (Gdx.input.isTouched(0) && x0 < 100 && y0 < 100) || (Gdx.input.isTouched(1) && x1 < 100 && y0 < 100);
        boolean rightButton = (Gdx.input.isTouched(0) && x0 > 140 && x0 < 240 && y0 < 100) || (Gdx.input.isTouched(1) && x1 > 140 && x1 < 240 && y0 < 100);
        boolean jumpButton = (Gdx.input.isTouched(0) && x0 > 700 && x0 < 800 && y0 < 100) || (Gdx.input.isTouched(1) && x1 > 700 && x1 < 800 && y0 < 100);
        boolean attackButton = (Gdx.input.isTouched(0) && x0 > 600 && x0 < 700 && y0 < 100) || (Gdx.input.isTouched(1) && x1 > 600 && x1 < 700 && y0 < 100);

        if (Gdx.input.isKeyPressed(Keys.J) || attackButton)
            isAttacking = true;
        else
            isAttacking = false;
        if ((Gdx.input.isKeyPressed(Keys.W) || jumpButton) && state != JUMP)
        {
            //jump.play(1f);
            state = JUMP;
            vel.y = JUMP_VELOCITY;
            grounded = false;
        }

        if (Gdx.input.isKeyPressed(Keys.A) || leftButton)
        {
            if (state != JUMP)
                state = RUN;
            dir = LEFT;
            accel.x = ACCELERATION * dir;
        }
        else if (Gdx.input.isKeyPressed(Keys.D) || rightButton)
        {
            if (state != JUMP)
                state = RUN;
            dir = RIGHT;
            accel.x = ACCELERATION * dir;
        }
        else
        {
            if (state != JUMP)
                state = IDLE;
            accel.x = 0;
        }
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
        // Reversed y-axis with pixmaps
        /*int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
        int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
        int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
        int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];*/
        int tile1 = tiles[p1x][p1y];
        int tile2 = tiles[p2x][p2y];
        int tile3 = tiles[p3x][p3y];
        int tile4 = tiles[p4x][p4y];

        if (tile1 == Map.TILE || tile1 == Map.BORDER || tile1 == Map.ROCK)
            r[0].set(p1x, p1y, 1, 1);
        else
            r[0].set(-1, -1, 0, 0);
        if (tile2 == Map.TILE || tile2 == Map.BORDER || tile2 == Map.ROCK)
            r[1].set(p2x, p2y, 1, 1);
        else
            r[1].set(-1, -1, 0, 0);
        if (tile3 == Map.TILE || tile3 == Map.BORDER || tile3 == Map.ROCK)
            r[2].set(p3x, p3y, 1, 1);
        else
            r[2].set(-1, -1, 0, 0);
        if (tile4 == Map.TILE || tile4 == Map.BORDER || tile4 == Map.ROCK)
            r[3].set(p4x, p4y, 1, 1);
        else
            r[3].set(-1, -1, 0, 0);

        //		r[4].x = map.platforms.get(2).bounds.x;
        //		r[4].y = map.platforms.get(2).bounds.y;
        //		r[4].width = map.platforms.get(2).bounds.width;
        //		r[4].height = map.platforms.get(2).bounds.height;
        //r[4].set(-1, -1, 0, 0);
    }

    public void dispose()
    {
        //splat.dispose();
    }
}