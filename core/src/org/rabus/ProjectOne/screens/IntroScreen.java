package org.rabus.ProjectOne.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IntroScreen extends BasicScreen
{
	AssetManager assets;
	SpriteBatch batch;
	BitmapFont font = new BitmapFont();
	float time = 0;

	public IntroScreen(Game game, AssetManager assets)
	{
		super(game);
		this.assets = assets;
	}

	@Override
	public void show()
	{
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "Press anything to proceed (should be loading info though ;p)", Gdx.app.getGraphics().getWidth() / 2 - 200, Gdx.app.getGraphics().getHeight() / 2);
		batch.end();

		time += delta;
		if (time > 1)
		{
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched())
			{
				game.setScreen(new GameScreen(game, assets, 0)); // If anything pressed or touched (android) moves on and it's playtime :) (loads 0 level)
			}
		}
	}

	@Override
	public void hide()
	{
		Gdx.app.debug("ProjectOne", "dispose intro");
		batch.dispose();
	}

}