package org.rabus.ProjectOne.renderers;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import org.rabus.ProjectOne.entities.Fluid;
import org.rabus.ProjectOne.entities.Map;

public class MapRenderer
{
    AssetManager assets;
    Map map;
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledRenderer;
    public OrthographicCamera cam;
    SpriteBatch batch = new SpriteBatch();
    //	float time = 0;
    //FrameBuffer buffer;
    //TextureRegion bufferRegion;
    //ImmediateModeRenderer10 renderer = new ImmediateModeRenderer10();
    //ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, true, 0);
    BitmapFont font = new BitmapFont();
    int[] backgroundLayers = {1, 2, 3, 4};
    int[] foregroundLayers = {5, 6, 7};
    float unitScale = 1 / 16f;
    float viewportWidth = 8f;
    float viewportHeight = 5f;
    TextureRegion[][] blockTiles;
    Animation playerLeft;
    Animation playerRight;
    Animation playerJumpLeft;
    Animation playerJumpRight;
    Animation playerIdleLeft;
    Animation playerIdleRight;
    Animation playerDead;
    Animation zap;
    TextureRegion checkpointSprite;
    Animation checkpoint;
    Animation spawn;
    Animation dying;
    Animation dyingMelt;
    TextureRegion spikes;
    Animation rocket;
    Animation energyball;
    Animation energyballExplosion;
    Animation fireballExplosion;
    TextureRegion rocketPad;
    TextureRegion endFlag;
    TextureRegion movingSpikes;
    TextureRegion platform;
    Animation spawner;
    Animation bluecrystal, greencrystal, redcrystal;
    Animation water, lava, acid;
    Texture background;
    FPSLogger fps = new FPSLogger();

    public MapRenderer(AssetManager assets, Map map)
    {
        this.assets = assets;
        this.map = map;
        this.tiledMap = assets.get("maps/map.tmx");
        tiledRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        /*if ((float) this.map.width < this.viewportWidth && (float) this.map.height < this.viewportHeight) // Changes viewport size according to map (if smaller)
        {
            this.viewportWidth = (float) this.map.width;
            this.viewportHeight = (float) this.map.height;
        }
        else if ((float) this.map.width < this.viewportWidth && (float) this.map.height > this.viewportHeight) // High vertical map
        {
            this.viewportWidth = (float) this.map.width;
        }
        else if ((float) this.map.width > this.viewportWidth && (float) this.map.height < this.viewportHeight) // Long horizontal map
        {
            this.viewportHeight = (float) this.map.height;
        }*/
        this.cam = new OrthographicCamera(viewportWidth, viewportHeight); // Viewport to be determined
        this.cam.position.set(map.player.pos.x, map.player.pos.y, 0);
        if (Gdx.app.getType() != ApplicationType.Android)
            this.cam.zoom = 1.0f; // Zoom for desktop (100%)
        else
            this.cam.zoom = 0.8f; // Zoom for android and other (60%)
        this.viewportWidth = this.viewportWidth * this.cam.zoom;
        this.viewportHeight = this.viewportHeight * this.cam.zoom;

        createAnimations();
    }

    float stateTime = 0;
    Vector3 lerpTarget = new Vector3();

    public void render(float deltaTime)
    {
        updateCamera(deltaTime);

        tiledRenderer.render(backgroundLayers);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        renderCheckpoints();
        //renderSpawners();
        //if (map.endFlag != null)
        //    batch.draw(endFlag, map.endFlag.bounds.x, map.endFlag.bounds.y, 1, 1);
        //renderMovingSpikes();
        //renderPlatforms();
        //renderPickupItems();
        renderCrystals();
        renderPlayer();
        //renderMonsters();
        //renderProjectiles();
        //renderFluids();
        //renderSpikes();
        batch.end();

        tiledRenderer.render(foregroundLayers);

        // Render Tiled Map Here
        fps.log();
    }

    private void updateCamera(float deltaTime)
    {
        float leftBound, rightBound, bottomBound, topBound; // Specifies camera movement bounds based on viewport and map dimensions
        leftBound = viewportWidth / 2;
        rightBound = leftBound + ((float) map.width - viewportWidth);
        bottomBound = viewportHeight / 2;
        topBound = bottomBound + ((float) map.height - viewportHeight);

        //cam.position.set(map.player.pos.x, map.player.pos.y, 0);
        cam.position.lerp(lerpTarget.set(map.player.pos.x, map.player.pos.y, 0), 3f * deltaTime); // Fluid camera (viewport) following player

        float cameraAdjustX, cameraAdjustY;

        // Important code, limiting bounds of camera movement
        if (cam.position.x <= leftBound)
        {
            cameraAdjustX = cam.position.x - leftBound;
            cam.position.x -= cameraAdjustX;
        }
        else if (cam.position.x >= rightBound)
        {
            cameraAdjustX = cam.position.x - rightBound;
            cam.position.x -= cameraAdjustX;
        }
        if (cam.position.y >= topBound)
        {
            cameraAdjustY = cam.position.y - topBound;
            cam.position.y -= cameraAdjustY;
        }
        else if (cam.position.y <= bottomBound)
        {
            cameraAdjustY = cam.position.y - bottomBound;
            cam.position.y -= cameraAdjustY;
        }

        cam.update();
        tiledRenderer.setView(cam);
    }

    private void createAnimations()
    {
        TextureAtlas atlas = assets.get("gfx/textures.atlas", TextureAtlas.class);
        // ##### backgrounds #####
        //		background = new Texture(Gdx.files.internal("gfx/wizardtower.png"));
        // ##### 20x20 basic ground tiles #####
        TextureRegion blockTileTexture = atlas.findRegion("tiles");
        blockTiles = new TextureRegion[(int) blockTileTexture.getRegionHeight() / 20][(int) blockTileTexture.getRegionWidth() / 20]; // Initialize blockTiles array
        blockTiles[0] = new TextureRegion(blockTileTexture).split(20, 20)[0];
        blockTiles[1] = new TextureRegion(blockTileTexture).split(20, 20)[1];
        blockTiles[2] = new TextureRegion(blockTileTexture).split(20, 20)[2];
        blockTiles[3] = new TextureRegion(blockTileTexture).split(20, 20)[3];
        blockTiles[4] = new TextureRegion(blockTileTexture).split(20, 20)[4];
        // ##### 20x20 fluids #####
        TextureRegion fluidTileTexture = atlas.findRegion("fluids");
        TextureRegion[] fluidTiles = new TextureRegion(fluidTileTexture).split(20, 20)[0]; // Initialize fluidTiles array
        water = new Animation(0.5f, fluidTiles[0], fluidTiles[1]);
        lava = new Animation(0.5f, fluidTiles[2], fluidTiles[3], fluidTiles[4], fluidTiles[5]);
        acid = new Animation(0.5f, fluidTiles[6], fluidTiles[7]);
        // ##### Player sprites #####
        TextureRegion playerTexture = assets.get("gfx/sprites.atlas", TextureAtlas.class).findRegion("player"); // Player character sprites
        TextureRegion[] split = new TextureRegion(playerTexture).split(20, 20)[0]; // Extracts sprites from sheet // Row 1
        TextureRegion[] mirror = new TextureRegion(playerTexture).split(20, 20)[0]; // Mirrors sprites for directional animations
        for (TextureRegion region : mirror)
            region.flip(true, false);
        playerIdleRight = new Animation(0.5f, split[0], split[1]);
        playerIdleLeft = new Animation(0.5f, mirror[0], mirror[1]);
        playerRight = new Animation(0.1f, split[2], split[3]);
        playerLeft = new Animation(0.1f, mirror[2], mirror[3]);
        playerJumpRight = new Animation(0.1f, split[4], split[5]);
        playerJumpLeft = new Animation(0.1f, mirror[4], mirror[5]);
        playerDead = new Animation(0.2f, split[0]);
        split = new TextureRegion(playerTexture).split(20, 20)[1]; // Row 2
        //spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
        dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
        split = new TextureRegion(playerTexture).split(20, 20)[2]; // Row 3
        dyingMelt = new Animation(0.1f, split[0], split[1], split[2], split[3], split[3], split[3], split[3], split[3]);
        split = new TextureRegion(playerTexture).split(20, 20)[3]; // Row 4
        spawn = new Animation(0.1f, split[0], split[1], split[2], split[3]);
        // ##### Normal (20x20) sprites #####
        TextureRegion normalObjectsTexture = atlas.findRegion("objects20x20");// Normal (20x20) objects sprites
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[0]; // Row 1
        energyball = new Animation(0.1f, split[0], split[1]);
        rocket = new Animation(0.1f, split[2], split[3]);
        rocketPad = split[4];
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[1]; // Row 2
        energyballExplosion = new Animation(0.1f, split[0], split[1], split[2], split[3], split[4], split[5]);
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[2]; // Row 3
        fireballExplosion = new Animation(0.1f, split[0], split[1], split[2], split[3], split[4], split[5]);
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[3]; // Row 4
        movingSpikes = split[0];
        platform = split[1];
        spawner = new Animation(0.2f, split[4], split[5]);
        spikes = split[2];
        endFlag = split[3];
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[4]; // Row 5
        checkpointSprite = split[1];
        checkpoint = new Animation(0.2f, split[1], split[2], split[3], split[4], split[4]);
        split = new TextureRegion(normalObjectsTexture).split(20, 20)[5]; // Row 6
        bluecrystal = new Animation(0.9f, split[0], split[1]);
        greencrystal = new Animation(0.9f, split[2], split[3]);
        redcrystal = new Animation(0.9f, split[4], split[5]);
        //split = new TextureRegion(normalObjectsTexture).split(20, 20)[6]; // Row 7
        //split = new TextureRegion(normalObjectsTexture).split(20, 20)[7]; // Row 8
        // ##### Higher (20x40) sprites #####
        //		TextureRegion higherObjectsTexture = atlas.findRegion("objects20x40"); // Higher game objects textures
        //		split = new TextureRegion(higherObjectsTexture).split(20, 40)[0]; // Row 1
        //		endFlag = split[0];
    }

    private void renderPlayer()
    {
        map.player.render(batch);
    }

    private void renderMonsters()
    {
        for (int i = 0; i < map.monsters.size; i++)
            map.monsters.get(i).render(batch);
    }

    private void renderProjectiles()
    {
        for (int i = 0; i < map.projectiles.size; i++)
            map.projectiles.get(i).render(batch);
    }

    private void renderCheckpoints()
    {
        for (int i = 0; i < map.checkpoints.size; i++)
        {
            if (map.checkpoints.get(i).active)
            {
                batch.draw(this.checkpoint.getKeyFrame(this.stateTime, true), map.checkpoints.get(i).pos.x, map.checkpoints.get(i).pos.y, 1, 1);
            }
            else
                batch.draw(checkpointSprite, map.checkpoints.get(i).pos.x, map.checkpoints.get(i).pos.y, 1, 1);
        }
    }

    private void renderCrystals()
    {
        for (int i = 0; i < map.crystals.size; i++)
            map.crystals.get(i).render(batch);
    }

    private void renderPickupItems()
    {
        for (int i = 0; i < map.pickupItems.size; i++)
            map.pickupItems.get(i).render(batch);
    }

    private void renderFluids()
    {
        for (int i = 0; i < map.fluids.size; i++)
        {
            TextureRegion frame = null;
            if (map.fluids.get(i).type == Fluid.WATER)
                frame = this.water.getKeyFrame(this.stateTime, true);
            else if (map.fluids.get(i).type == Fluid.LAVA)
                frame = this.lava.getKeyFrame(this.stateTime, true);
            else if (map.fluids.get(i).type == Fluid.ACID)
                frame = this.acid.getKeyFrame(this.stateTime, true);
            batch.draw(frame, map.fluids.get(i).pos.x, map.fluids.get(i).pos.y, 1, 1);
        }
    }

    private void renderSpikes()
    {
        for (int i = 0; i < map.spikes.size; i++)
            map.spikes.get(i).render(batch);
    }

    private void renderSpawners()
    {
        for (int i = 0; i < map.spawners.size; i++)
            batch.draw(this.spawner.getKeyFrame(this.stateTime / 4, true), map.spawners.get(i).pos.x, map.spawners.get(i).pos.y, 1, 1);
    }

    private void renderMovingSpikes()
    {
        for (int i = 0; i < map.movingSpikes.size; i++)
            batch.draw(movingSpikes, map.movingSpikes.get(i).pos.x, map.movingSpikes.get(i).pos.y, 0.5f, 0.5f, 1, 1, 1, 1, map.movingSpikes.get(i).angle);
    }

    //	private void renderPlatforms()
    //	{
    //		for (int i = 0; i < map.platforms.size; i++)
    //		{
    //			batch.draw(this.platform, map.platforms.get(i).pos.x, map.platforms.get(i).pos.y, 1, 1);
    //		}
    //	}

    public void resize(int width, int height)
    {
    }

    public void dispose()
    {
        batch.dispose();
    }
}
