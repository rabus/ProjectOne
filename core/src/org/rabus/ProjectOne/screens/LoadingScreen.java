package org.rabus.ProjectOne.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class LoadingScreen extends BasicScreen
{
    AssetManager assets;
    SpriteBatch batch;
    BitmapFont font = new BitmapFont();

    public LoadingScreen(Game game, AssetManager assets)
    {
        super(game);
        this.assets = assets;
    }

    @Override
    public void show()
    {
        loadAssets();
        batch = new SpriteBatch();
        //batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void loadAssets() // Definitions for all in-game assets to be loaded by AssetManager
    {
        // Music
        //assets.load("msx/music1.ogg", Music.class);

        // Sounds
        assets.load("sfx/splat.wav", Sound.class);
        assets.load("sfx/pickup_crystal.wav", Sound.class);
        assets.load("sfx/explode.wav", Sound.class);

        // Levels - maximum size: 128x128
        assets.load("levels/level0.png", Pixmap.class);
        assets.load("levels/level1.png", Pixmap.class);
        //assets.load("levels/level2.png", Pixmap.class);

        // Maps
        assets.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assets.load("maps/map0.tmx", TiledMap.class);
        assets.load("maps/map.tmx", TiledMap.class);

        // Fonts
        assets.load("gfx/fonts/verdana16.fnt", BitmapFont.class);
        assets.load("gfx/fonts/verdana24.fnt", BitmapFont.class);
        assets.load("gfx/fonts/verdana32.fnt", BitmapFont.class);

        // Controls
        assets.load("gfx/controls/controls.png", Texture.class);

        // Screens
        //assets.load("gfx/screens/title.png", Texture.class);
        //assets.load("gfx/screens/intro.png", Texture.class);
        //assets.load("gfx/screens/gameover.png", Texture.class);

        // Textures
        assets.load("gfx/textures.atlas", TextureAtlas.class);
        assets.load("gfx/sprites.atlas", TextureAtlas.class);
        assets.load("gfx/backgrounds.atlas", TextureAtlas.class);
    }

    @Override
    public void render(float delta)
    {
        if (assets.update()) // Updates assets loading and returns true is finished
        {
            Gdx.app.log("ProjectOne", "All assets loaded successfully ...");
            //game.setScreen(new MainMenu(game, assets)); // Loads main menu screen after loading assets
            game.setScreen(new GameScreen(game, assets, 0)); // If anything pressed or touched (android) moves on and it's playtime :) (loads 0 level)
        }
        else
        {
            float progress = assets.getProgress() * 100;
            Gdx.gl.glClearColor(0f, 0f, 0f, 1);
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            batch.begin();
            font.draw(batch, Float.toString(progress) + " %", 400, 240); // Progress "bar" (percentage for now)
            font.draw(batch, Integer.toString(assets.getLoadedAssets()) + " loaded / " + Integer.toString(assets.getQueuedAssets()) + " to go ...", 360, 220);
            batch.end();
        }
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("ProjectOne", "dispose loading screen");
        batch.dispose();
        font.dispose();
    }
}
