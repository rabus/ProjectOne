package org.rabus.ProjectOne.spatials;

import com.apollo.Layer;
import com.apollo.annotate.InjectComponent;
import com.apollo.components.Transform;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.newdawn.slick.Image;

public class PlayerNode extends BasicNode
{
    @InjectComponent
    Transform transform;
    private Image shipImage;

    public PlayerNode(Image ship)
    {
        this.shipImage = ship;
    }

    @Override
    public void initialize()
    {
    }

    @Override
    protected void attachChildren()
    {
        //addChild(new HealthbarSpatial());
    }

    @Override
    public Layer getLayer()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render(SpriteBatch batch)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
