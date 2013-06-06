package org.rabus.ProjectOne.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.rabus.ProjectOne.entities.Map;

public class ControlsRenderer
{
    AssetManager assets;
    Map map;
    SpriteCache cache;
    int cacheIndex;
    SpriteBatch batch;
    TextureRegion left;
    TextureRegion right;
    TextureRegion jump;
    TextureRegion attack;
    Texture texture;

    public ControlsRenderer(AssetManager assets, Map map)
    {
        this.assets = assets;
        this.map = map;
        loadAssets();
    }

    private void loadAssets()
    {
        texture = assets.get("gfx/controls/controls.png", Texture.class);
        TextureRegion[] buttons = TextureRegion.split(texture, 60, 60)[0];
        left = buttons[0];
        right = buttons[1];
        jump = buttons[2];
        attack = buttons[3];
        cache = new SpriteCache();
        cache.beginCache();
        cache.add(left, 20, 20);
        cache.add(right, 160, 20);
        cache.add(attack, 620, 20);
        cache.add(jump, 720, 20);
        cacheIndex = cache.endCache();
        cache.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
    }

    public void render()
    {
//		if (Gdx.app.getType() != ApplicationType.Android)
//			return;
//		else
        //{
        Gdx.gl.glEnable(GL10.GL_BLEND);
        cache.begin();
        cache.draw(cacheIndex);
        cache.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
        //}
    }

    public void dispose()
    {
        cache.dispose();
    }
}
