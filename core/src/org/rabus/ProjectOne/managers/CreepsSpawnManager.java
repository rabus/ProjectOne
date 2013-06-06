package org.rabus.ProjectOne.managers;

import com.apollo.Entity;
import com.apollo.components.Transform;
import com.apollo.managers.Manager;
import com.apollo.utils.Timer;
import org.rabus.ProjectOne.screens.GameScreen;

public class CreepsSpawnManager extends Manager
{
    private Timer spawnTimer;
    private GameScreen screen;

    public CreepsSpawnManager(GameScreen screen)
    {
        this.screen = screen;
    }

    @Override
    public void initialize()
    {
        spawnTimer = new Timer(1000, true)
        {
            @Override
            public void execute()
            {
                Entity meleeCreep = world.createEntity("MeleeCreep");
                meleeCreep.getComponent(Transform.class).setLocation((float) Math.random() * screen.tiledLayer.getWidth(), 60);
                world.addEntity(meleeCreep);
                world.addEntity(meleeCreep);
                world.addEntity(meleeCreep);

                Entity rangedCreep = world.createEntity("RangedCreep");
                rangedCreep.getComponent(Transform.class).setLocation((float) Math.random() * screen.tiledLayer.getWidth(), 60);
                world.addEntity(rangedCreep);
            }
        };
    }

    @Override
    public void update(int delta)
    {
        spawnTimer.update(delta);
    }

}
