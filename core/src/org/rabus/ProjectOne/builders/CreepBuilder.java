package org.rabus.ProjectOne.builders;

import com.badlogic.gdx.Gdx;
import org.newdawn.slick.Image;

import com.apollo.Component;
import com.apollo.Entity;
import com.apollo.EntityBuilder;
import com.apollo.EventHandler;
import com.apollo.World;
import com.apollo.annotate.InjectComponent;
import com.apollo.components.Transform;
import com.apollo.managers.GroupManager;
import com.apollo.utils.Timer;
import org.rabus.ProjectOne.Groups;
import org.rabus.ProjectOne.components.Movement;
import org.rabus.ProjectOne.spatials.CreepSpatial;

public class CreepBuilder implements EntityBuilder
{
    private Image ship1;

    public CreepBuilder(Image ship1)
    {
        this.ship1 = ship1;
    }

    @Override
    public Entity buildEntity(final World world)
    {
        final Entity e = new Entity(world);
        e.setComponent(new Transform());
        e.setComponent(new CreepSpatial(ship1));
        e.setComponent(new Movement(Math.random() > 0.5 ? 0.1f : -0.1f, 0f));

        // A little AI controller to manage movement.
        e.setComponent(createMovementAI());

        // A little AI controller to shoot.
        e.setComponent(createShootAI(world));

        e.addEventHandler("EXPIRED", new EventHandler()
        {
            @Override
            public void handleEvent()
            {
                System.out.println("Bullet has expired");
            }
        });

        e.addEventHandler("EXPLODED", new EventHandler()
        {
            @Override
            public void handleEvent()
            {
                Entity explosion = world.createEntity("ShipExplosion");
                explosion.getComponent(Transform.class).setLocation(e.getComponent(Transform.class));
                world.addEntity(explosion);
            }
        });

        world.getManager(GroupManager.class).setGroup(e, Groups.ComputerCreeps);

        return e;
    }

    private Component createShootAI(final World world)
    {
        return new Component()
        {
            @InjectComponent
            Transform transform;

            private Timer expiresTimer;

            @Override
            public void initialize()
            {
                expiresTimer = new Timer(1500, true)
                {
                    @Override
                    public void execute()
                    {
                        Entity bullet = world.createEntity("Bullet");
                        bullet.getComponent(Transform.class).setLocation(transform);
                        bullet.getComponent(Movement.class).setVectors(0, 0.2f);
                        world.getManager(GroupManager.class).setGroup(bullet, Groups.ComputerProjectiles);
                        world.addEntity(bullet);
                    }
                };
            }

            @Override
            public void update(int delta)
            {
                expiresTimer.update(delta);
            }
        };
    }

    private Component createMovementAI()
    {
        return new Component()
        {
            @InjectComponent
            Movement movement;
            @InjectComponent
            Transform transform;

            @Override
            public void update(int delta)
            {
                if (transform.getX() < 0)
                {
                    transform.setX(0);
                    movement.setVx(-movement.getVx());
                }
                else if (transform.getX() > Gdx.graphics.getHeight())
                {
                    transform.setX(Gdx.graphics.getWidth());
                    movement.setVx(-movement.getVx());
                }
            }
        };
    }


}
