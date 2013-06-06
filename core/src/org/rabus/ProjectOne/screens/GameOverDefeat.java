package org.rabus.ProjectOne.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverDefeat extends BasicScreen
{
	SpriteBatch batch;
	BitmapFont font = new BitmapFont();
	float time = 0;
	AssetManager assets;

	public GameOverDefeat(Game game, AssetManager assets)
	{
		super(game);
		this.assets = assets;
	}

	@Override
	public void show()
	{
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		//assets.get("msx/8bit.ogg", Music.class).stop();
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "It seems you lost, bummer xD", Gdx.app.getGraphics().getWidth() / 2 - 200, Gdx.app.getGraphics().getHeight() / 2);
		batch.end();

		time += delta;
		if (time > 1)
		{
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched())
			{
				game.setScreen(new MainMenu(game, assets));
				//assets.clear();
				//game.setScreen(new LoadingScreen(game, assets));
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