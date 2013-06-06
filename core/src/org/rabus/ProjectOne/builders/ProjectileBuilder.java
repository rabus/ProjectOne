package org.rabus.ProjectOne.builders;

import com.apollo.Entity;
import com.apollo.EntityBuilder;
import com.apollo.EventHandler;
import com.apollo.World;
import com.apollo.components.Transform;
import org.rabus.ProjectOne.components.Expires;
import org.rabus.ProjectOne.components.Movement;
import org.rabus.ProjectOne.spatials.ProjectileSpatial;

public class ProjectileBuilder implements EntityBuilder
{

    @Override
    public Entity buildEntity(final World world)
    {
        final Entity e = new Entity(world);
        e.setComponent(new Transform());
        e.setComponent(new ProjectileSpatial());
        e.setComponent(new Movement());
        e.setComponent(new Expires(5000));

        e.addEventHandler("EXPIRED", new EventHandler()
        {
            @Override
            public void handleEvent()
            {
            }
        });

        e.addEventHandler("EXPLODED", new EventHandler()
        {
            @Override
            public void handleEvent()
            {
                Entity explosion = world.createEntity("ProjectileExplosion");
                explosion.getComponent(Transform.class).setLocation(e.getComponent(Transform.class));
                world.addEntity(explosion);
            }
        });

        return e;
    }


}
