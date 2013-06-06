package org.rabus.ProjectOne.builders;

import com.apollo.Entity;
import com.apollo.EntityBuilder;
import com.apollo.EventHandler;
import com.apollo.World;
import com.apollo.components.Transform;
import org.rabus.ProjectOne.components.Expires;
import org.rabus.ProjectOne.spatials.ExplosionSpatial;

public class ExplosionBuilder implements EntityBuilder
{

    private int radius;

    public ExplosionBuilder(int radius)
    {
        this.radius = radius;
    }

    @Override
    public Entity buildEntity(World world)
    {
        Entity e = new Entity(world);
        e.setComponent(new Transform());
        e.setComponent(new ExplosionSpatial(radius));
        e.setComponent(new Expires(2000));

        e.addEventHandler("EXPIRED", new EventHandler()
        {
            @Override
            public void handleEvent()
            {
            }
        });

        return e;
    }


}