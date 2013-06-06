package org.rabus.ProjectOne.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.rabus.ProjectOne.entities.Map;

public class BackgroundRenderer
{
    AssetManager assets;
    Map map;
    SpriteBatch batch;

    public BackgroundRenderer(AssetManager assets, Map map)
    {
        this.assets = assets;
        this.map = map;
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render()
    {
        Gdx.gl.glEnable(GL10.GL_BLEND);
        batch.begin();
        batch.draw(assets.get("gfx/backgrounds.atlas", TextureAtlas.class).findRegion("sky"), 0, 0);
        batch.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
    }

    public void dispose()
    {
        batch.dispose();
    }
}
