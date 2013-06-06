package org.rabus.ProjectOne.components;

import com.apollo.Component;
import com.apollo.annotate.InjectComponent;
import com.apollo.components.Transform;

public class Movement extends Component
{
    @InjectComponent
    Transform transform;

    private float vx, vy;

    public Movement()
    {
    }

    public Movement(float vx, float vy)
    {
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public void update(int delta)
    {
        transform.addX(delta * vx);
        transform.addY(delta * vy);
    }

    public void setVectors(float vx, float vy)
    {
        this.vx = vx;
        this.vy = vy;
    }

    public float getVx()
    {
        return vx;
    }

    public void setVx(float vx)
    {
        this.vx = vx;
    }

    public float getVy()
    {
        return vy;
    }

    public void setVy(float vy)
    {
        this.vy = vy;
    }


}
