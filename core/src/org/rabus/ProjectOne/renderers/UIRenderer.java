package org.rabus.ProjectOne.renderers;

import com.apollo.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.rabus.ProjectOne.entities.Map;

public class UIRenderer
{
    AssetManager assets;
    Map map;
    SpriteBatch batch;
    BitmapFont font16, font24;
    TextureRegion heart;

    public UIRenderer(AssetManager assets, Map map, World world)
    {
        this.assets = assets;
        this.map = map;
        heart = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("objects12x12").split(12, 12)[0][0];
        font24 = assets.get("gfx/fonts/verdana24.fnt", BitmapFont.class);
        font16 = assets.get("gfx/fonts/verdana16.fnt", BitmapFont.class);
        //font.setScale(0.7f);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
    }

    public void render()
    {
        batch.begin();
        font24.setColor(0, 0, 0, 1);
        //font24.draw(batch, "Scores:   ", 20, 460);
        //font24.draw(batch, "Crystals: ", 20, 440);
        for (int i = 1; i <= map.player.health; i++)
        {
            batch.draw(heart, i * 15 + 5, 440, 30, 30); // 440 <-> 380
        }
        //font24.setColor(1, 0.1f, 0.1f, 1);
        //font24.draw(batch, Integer.toString(map.player.score), 140, 460);
        //font24.draw(batch, Integer.toString(map.player.crystals), 140, 440);
        font16.setColor(1, 1, 1, 1);
        font16.draw(batch, "ProjectOne pre-alpha by Adam Rabiega", 470, 20);
        font16.setColor(0, 0, 0, 1);
        font16.draw(batch, "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()), 700, 470);
        //font16.draw(batch, "PL0: " + Integer.toString((int) this.map.player.pos.x) + " " + Integer.toString((int) this.map.player.pos.y), 700, 450);
        font16.draw(batch, "MS0: " + Integer.toString((int) Gdx.input.getX()) + " " + Integer.toString((int) Gdx.input.getY()), 700, 450);
        //font16.draw(batch, "MS0: " + Integer.toString((int) (Gdx.input.getX() / 80)) + " " + Integer.toString((int) (Gdx.input.getY() / 80)), 700, 430);
        batch.end();
    }

    public void dispose()
    {
        batch.dispose();
    }
}
