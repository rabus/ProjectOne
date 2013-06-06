package org.rabus.ProjectOne.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenu extends BasicScreen
{
	AssetManager assets;
	Music music;
	SpriteBatch batch;
	BitmapFont font = new BitmapFont();
	float time = 0;

	public MainMenu(Game game, AssetManager assets)
	{
		super(game);
		this.assets = assets;
	}

	@Override
	public void show()
	{
		//		if (assets.isLoaded("msx/music1.ogg"))
		//		{
		//			assets.get("msx/music1.ogg", Music.class).setLooping(true);
		//			assets.get("msx/music1.ogg", Music.class).stop();
		//			assets.get("msx/music1.ogg", Music.class).play();
		//		}
		Gdx.input.setCatchBackKey(true);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "This is Project Zero, press anything :)", Gdx.app.getGraphics().getWidth() / 2 - 200, Gdx.app.getGraphics().getHeight() / 2);
		batch.end();

		time += delta;
		if (time > 1)
		{
			if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK))
			{
				Gdx.app.exit(); // If escape or back key (android) is pressed, then exits the game
				//game.setScreen(new MainMenu(game));
			}
			else
				if (Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched())
				{
					game.setScreen(new IntroScreen(game, assets)); // If any key is pressed or screen touched (android) loads next screen (with intro)
				}
		}
	}

	@Override
	public void hide()
	{
		Gdx.app.debug("ProjectOne", "dispose main menu");
		batch.dispose();
	}
}
