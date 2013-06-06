package org.rabus.ProjectOne.spatials;

import com.apollo.components.spatial.Spatial;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BasicSpatial extends Spatial<SpriteBatch>
{
    public abstract void render(SpriteBatch batch);
}