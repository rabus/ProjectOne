package org.rabus.ProjectOne.screens;

import com.apollo.Entity;
import com.apollo.World;
import com.apollo.managers.GroupManager;
import com.apollo.managers.RenderManager;
import com.apollo.managers.TagManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.rabus.ProjectOne.entities.Map;
import org.rabus.ProjectOne.entities.Monster;
import org.rabus.ProjectOne.entities.Player;
import org.rabus.ProjectOne.managers.CollisionManager;
import org.rabus.ProjectOne.managers.CreepsSpawnManager;
import org.rabus.ProjectOne.renderers.BackgroundRenderer;
import org.rabus.ProjectOne.renderers.ControlsRenderer;
import org.rabus.ProjectOne.renderers.UIRenderer;
import org.rabus.ProjectOne.renderers.MapRenderer;
import org.rabus.ProjectOne.systems.*;

/**
 * @author Adam Rabiega
 */
public class GameScreen extends BasicScreen implements InputProcessor
{
    AssetManager assets = new AssetManager();
    Map map;
    Player player;

    private World world;
    private RenderManager renderManager;
    private TagManager tagManager;
    private GroupManager groupManager;

    private OrthographicCamera camera;
    private Vector2 playerPosition;

    int[] backgroundLayers = {1, 2, 3, 4};
    int[] foregroundLayers = {5, 6, 7};
    float unitScale = 1 / 32f;
    float viewportWidth = 16f;
    float viewportHeight = 10f;

    SpriteBatch batch = new SpriteBatch();
    OrthogonalTiledMapRenderer tiledRenderer;
    TiledMap tiledMap;
    public TiledMapTileLayer tiledLayer;
    MapRenderer mapRenderer;
    BackgroundRenderer bgRenderer;
    ControlsRenderer controlRenderer;
    UIRenderer uiRenderer;
    long renderTime;
    int currentLevel;

    //static int howManyLevels = 2; // Hard set level number (should change to dynamic checking!!) - Changed below

    public GameScreen(Game game, AssetManager assets, int currentLevel)
    {
        super(game);
        //this.camera = new OrthographicCamera(viewportWidth, viewportHeight);
        this.assets = assets;
        this.currentLevel = currentLevel;
        this.player = new Player();
        this.tiledMap = assets.get("maps/map.tmx");
        tiledRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        initializeCamera();
        initializeWorld();
    }

    public GameScreen(Game game, AssetManager assets, int currentLevel, Player player)
    {
        super(game);
        //this.camera = new OrthographicCamera(viewportWidth, viewportHeight);
        this.assets = assets;
        this.currentLevel = currentLevel;
        this.player = player;
        this.tiledMap = assets.get("maps/map0.tmx");
        tiledRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        initializeCamera();
        initializeWorld();
    }

    private void initializeCamera()
    {
        this.camera = new OrthographicCamera(viewportWidth, viewportHeight); // Viewport to be determined
        //this.camera.position.set(playerPosition.x, playerPosition.y, 0);
        if (Gdx.app.getType() != Application.ApplicationType.Android)
            this.camera.zoom = 1.0f; // Zoom for desktop (100%)
        else
            this.camera.zoom = 0.8f; // Zoom for android and other (60%)
        this.viewportWidth = this.viewportWidth * this.camera.zoom;
        this.viewportHeight = this.viewportHeight * this.camera.zoom;
    }

    private void initializeWorld()
    {
        world = new World();
        renderManager = new RenderManager(Gdx.graphics);
        tagManager = new TagManager();
        groupManager = new GroupManager();

        world.setManager(renderManager);
        world.setManager(tagManager);
        world.setManager(groupManager);
        world.setManager(new CreepsSpawnManager(this));
        world.setManager(new CollisionManager(this));

        //playerPosition = world.getManager(GroupManager.class).getEntities(Constants.org.rabus.ProjectOne.Groups.PLAYER_SHIP).get(0).getComponent(Position.class);
        //Gdx.input.setInputProcessor(new PlayerInputSystem(camera));
        tiledLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        //playerPosition.x = 10;
        //playerPosition.y = 10;
    }

    private void updateCamera(float deltaTime)
    {
        Vector3 lerpTarget = new Vector3();
        float leftBound, rightBound, bottomBound, topBound; // Specifies camera movement bounds based on viewport and map dimensions
        leftBound = viewportWidth / 2;
        rightBound = leftBound + ((float) tiledLayer.getWidth() - viewportWidth);
        bottomBound = viewportHeight / 2;
        topBound = bottomBound + ((float) tiledLayer.getHeight() - viewportHeight);

        //cam.position.set(map.player.pos.x, map.player.pos.y, 0);
        //camera.position.lerp(lerpTarget.set(playerPosition.x, playerPosition.y, 0), 3f * deltaTime); // Fluid camera (viewport) following player

        float cameraAdjustX, cameraAdjustY;

        // Important code, limiting bounds of camera movement
        if (camera.position.x <= leftBound)
        {
            cameraAdjustX = camera.position.x - leftBound;
            camera.position.x -= cameraAdjustX;
        }
        else if (camera.position.x >= rightBound)
        {
            cameraAdjustX = camera.position.x - rightBound;
            camera.position.x -= cameraAdjustX;
        }
        if (camera.position.y >= topBound)
        {
            cameraAdjustY = camera.position.y - topBound;
            camera.position.y -= cameraAdjustY;
        }
        else if (camera.position.y <= bottomBound)
        {
            cameraAdjustY = camera.position.y - bottomBound;
            camera.position.y -= cameraAdjustY;
        }

        camera.update();
        tiledRenderer.setView(camera);
    }

    @Override
    public void show()
    {
        Gdx.input.setCatchBackKey(true);
        //Gdx.input.setInputProcessor(this);

        //map = new Map(assets, assets.get("levels/level" + Integer.toString(currentLevel) + ".png", Pixmap.class));
        map = new Map(assets, assets.get("maps/map" + Integer.toString(currentLevel) + ".tmx", TiledMap.class));

        map.player.health = this.player.health;
        map.player.crystals = this.player.crystals;
        map.player.score = this.player.score;

        //bgRenderer = new BackgroundRenderer(assets, map);
        //mapRenderer = new MapRenderer(assets, map);
        //controlRenderer = new ControlsRenderer(assets, map);
        //uiRenderer = new UIRenderer(assets, map, world);

        renderTime = System.nanoTime();
    }

    @Override
    public void render(float delta)
    {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
        map.update(delta); // Updates game logic (there is logic? ;p) // Most heavy

        Gdx.gl.glClearColor(0.529f, 0.808f, 0.98f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        updateCamera(delta);
        //camera.update();

        //world.setDelta(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) // Speed up game while holding spacebar
        {
            for (int i = 0; 10 > i; i++)
            {
                world.update((int) delta);
            }
        }
        world.update((int) delta);

        batch.begin();
        batch.setColor(0, 0, 0, 0.5f);
        batch.end();

        tiledRenderer.render(backgroundLayers);

        // Render sprites

        tiledRenderer.render(foregroundLayers);

        // Render health and hud

        //bgRenderer.render(); // Background mapRenderer

        //mapRenderer.render(delta); // Game content render

        //controlRenderer.render(); // Android controls render

        //uiRenderer.render(); // User interface (health, score, fps)


        if (map.player.health == 0)
            game.setScreen(new GameScreen(game, assets, 0));
        //game.setScreen(new GameOverDefeat(game, assets)); // Player dies, game over

        if (map.player.bounds.overlaps(map.endFlag.bounds)) // Player finds exit doors (or flag), level complete ;p
        {
            if (assets.isLoaded("levels/level" + Integer.toString(currentLevel + 1) + ".png", Pixmap.class))
            {
                currentLevel += 1;
                game.setScreen(new GameScreen(game, assets, currentLevel, map.player));
                //assets.unload("levels/level" + Integer.toString(currentLevel - 1) + ".png"); // Unload previous level. Have to check this
            }
            else
                game.setScreen(new GameScreen(game, assets, 0));
            //game.setScreen(new GameOverVictory(game, assets)); // If no more levels, then game is complete! (have to add Game Complete Screen :P)
        }

        if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK))
        {
            //Gdx.app.exit();
            //game.setScreen(new MainMenu(game, assets)); // Use with full game
            Gdx.app.exit();
            //game.setScreen(new GameScreen(game, assets, 0));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        //mapRenderer.resize(width, height);
        //		this.width = width;
        //		this.height = height;
        //		if (width == 480 && height == 320) {
        //			cam = new OrthographicCamera(700, 466);
        //			this.width = 700;
        //			this.height = 466;
        //		} else if (width == 320 && height == 240) {
        //			cam = new OrthographicCamera(700, 525);
        //			this.width = 700;
        //			this.height = 525;
        //		} else if (width == 400 && height == 240) {
        //			cam = new OrthographicCamera(800, 480);
        //			this.width = 800;
        //			this.height = 480;
        //		} else if (width == 432 && height == 240) {
        //			cam = new OrthographicCamera(700, 389);
        //			this.width = 700;
        //			this.height = 389;
        //		} else if (width == 960 && height == 640) {
        //			cam = new OrthographicCamera(800, 533);
        //			this.width = 800;
        //			this.height = 533;
        //		}  else if (width == 1366 && height == 768) {
        //			cam = new OrthographicCamera(1280, 720);
        //			this.width = 1280;
        //			this.height = 720;
        //		} else if (width == 1366 && height == 720) {
        //			cam = new OrthographicCamera(1280, 675);
        //			this.width = 1280;
        //			this.height = 675;
        //		} else if (width == 1536 && height == 1152) {
        //			cam = new OrthographicCamera(1366, 1024);
        //			this.width = 1366;
        //			this.height = 1024;
        //		} else if (width == 1920 && height == 1152) {
        //			cam = new OrthographicCamera(1366, 854);
        //			this.width = 1366;
        //			this.height = 854;
        //		} else if (width == 1920 && height == 1200) {
        //			cam = new OrthographicCamera(1366, 800);
        //			this.width = 1280;
        //			this.height = 800;
        //		} else if (width > 1280) {
        //			cam = new OrthographicCamera(1280, 768);
        //			this.width = 1280;
        //			this.height = 768;
        //		} else if (width < 800) {
        //			cam = new OrthographicCamera(800, 480);
        //			this.width = 800;
        //			this.height = 480;
        //		} else {
        //			cam = new OrthographicCamera(width, height);
        //		}
        //		cam.position.x = 400;
        //		cam.position.y = 240;
        //		cam.update();
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("ProjectOne", "dispose game screen");
        player.dispose();
        map.dispose();
        mapRenderer.dispose();
        controlRenderer.dispose();
        uiRenderer.dispose();
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
        Gdx.app.debug("ProjectOne", "dispose game screen");
        map.dispose();
        mapRenderer.dispose();
        controlRenderer.dispose();
        uiRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Keys.F1) // F1 adds monsters at spawner location
            if (map.spawners.size >= 1)
                map.monsters.add(new Monster(assets, map, map.spawners.get(0).pos.x, map.spawners.get(0).pos.y, Monster.ORC));
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
