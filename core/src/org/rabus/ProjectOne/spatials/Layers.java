package org.rabus.ProjectOne.spatials;

import com.apollo.Layer;

public enum Layers implements Layer
{
    Background,
    Ships,
    Effects,
    Projectiles,
    Interface;

    @Override
    public int getLayerId()
    {
        return ordinal();
    }

}
