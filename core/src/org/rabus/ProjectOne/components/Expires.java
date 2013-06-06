package org.rabus.ProjectOne.components;

import com.apollo.Component;
import com.apollo.World;
import com.apollo.utils.Timer;

public class Expires extends Component
{
    private Timer expiresTimer;

    public Expires(int lifetimeMilliseconds)
    {
        expiresTimer = new Timer(lifetimeMilliseconds)
        {
            @Override
            public void execute()
            {
                expire();
            }
        };
    }

    @Override
    public void update(int delta)
    {
        expiresTimer.update(delta);
    }

    private void expire()
    {
        World world = owner.getWorld();
        world.deleteEntity(owner);
        owner.fireEvent("EXPIRED");
    }
}
