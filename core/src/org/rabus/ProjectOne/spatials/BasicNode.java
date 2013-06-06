package org.rabus.ProjectOne.spatials;

import com.apollo.components.spatial.Node;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class BasicNode extends Node<SpriteBatch>
{
    public abstract void render(SpriteBatch batch);
}