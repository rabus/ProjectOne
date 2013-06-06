package org.rabus.ProjectOne.managers;

import com.apollo.Entity;
import com.apollo.annotate.InjectManager;
import com.apollo.components.Transform;
import com.apollo.managers.GroupManager;
import com.apollo.managers.Manager;
import com.apollo.managers.TagManager;
import com.apollo.utils.Bag;
import org.rabus.ProjectOne.Groups;
import org.rabus.ProjectOne.Tags;
import org.rabus.ProjectOne.components.Health;
import org.rabus.ProjectOne.screens.GameScreen;

public class CollisionManager extends Manager
{
    @InjectManager
    GroupManager groupManager;

    @InjectManager
    TagManager tagManager;

    private GameScreen screen;

    private Bag<Entity> computerProjectiles;

    private Bag<Entity> computerCreeps;

    private Entity player;

    private Bag<Entity> playerProjectiles;

    public CollisionManager(GameScreen screen)
    {
        this.screen = screen;
    }

    @Override
    public void initialize()
    {
        computerProjectiles = groupManager.getEntityGroup(Groups.ComputerProjectiles);
        computerCreeps = groupManager.getEntityGroup(Groups.ComputerCreeps);
        player = tagManager.getEntity(Tags.Player);
        playerProjectiles = groupManager.getEntityGroup(Groups.PlayerProjectiles);
    }

    @Override
    public void update(int delta)
    {
        // Player bullets collide with computer ships.
        for (int a = 0; playerProjectiles.size() > a; a++)
        {
            Entity playerProjectile = playerProjectiles.get(a);
            Transform pt = playerProjectile.getComponent(Transform.class);
            for (int i = 0; computerCreeps.size() > i; i++)
            {
                Entity cs = computerCreeps.get(i);
                Transform ct = cs.getComponent(Transform.class);
                if (pt.getDistanceTo(ct) < 20)
                {
                    world.deleteEntity(playerProjectile);
                    world.deleteEntity(cs);
                    playerProjectile.fireEvent("EXPLODED");
                    cs.fireEvent("EXPLODED");
                }
            }
        }

        // Computer bullets collide with player ship.
        Transform pt = player.getComponent(Transform.class);
        for (int i = 0; computerProjectiles.size() > i; i++)
        {
            Entity cb = computerProjectiles.get(i);
            Transform ct = cb.getComponent(Transform.class);
            if (pt.getDistanceTo(ct) < 20)
            {
                world.deleteEntity(cb);
                cb.fireEvent("EXPLODED");
                player.getComponent(Health.class).addDamage(13); // 13 is bad luck.
            }
        }
    }

}
