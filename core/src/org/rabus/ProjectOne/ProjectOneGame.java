package org.rabus.ProjectOne;

import org.rabus.ProjectOne.screens.LoadingScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

/** @author Adam Rabiega */
public class ProjectOneGame extends Game
{
	AssetManager assets = new AssetManager();

	@Override
	public void create()
	{
		setScreen(new LoadingScreen(this, assets));
	}

	@Override
	public void dispose()
	{
		assets.dispose();
		Gdx.app.log("ProjectOne", "Assets disposed, memory cleaned!");
	}
}
