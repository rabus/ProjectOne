package org.rabus.ProjectOne.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import org.rabus.ProjectOne.entities.Entity;

public class PlayerInputSystem implements InputProcessor
{
    private static final float HorizontalThrusters = 300;
    private static final float HorizontalMaxSpeed = 300;
    private static final float VerticalThrusters = 200;
    private static final float VerticalMaxSpeed = 200;
    private static final float FireRate = 0.1f;

    private boolean up, down, left, right;
    private boolean shoot;
    private float timeToFire;

    private float destinationX, destinationY;
    private OrthographicCamera camera;
    private Vector3 mouseVector;
    private Vector3 direction = new Vector3();
    private Vector3 position;

    public PlayerInputSystem(OrthographicCamera camera)
    {
        //super(null);
        this.camera = camera;
        this.mouseVector = new Vector3();
    }

    protected void initialize()
    {
        Gdx.input.setInputProcessor(this);
    }

    protected void process(Entity e)
    {

        mouseVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseVector);

        destinationX = mouseVector.x;
        destinationY = mouseVector.y;

        //float angleInRadians = Utils.angleInRadians(position.x, position.y, destinationX, destinationY);

        //position.x += TrigLUT.cos(angleInRadians) * 500f * world.getDelta();
        //position.y += TrigLUT.sin(angleInRadians) * 500f * world.getDelta();

        //position.x = mouseVector.x; // If mouse controlled
        //position.y = mouseVector.y;

        /*if (up)
        {
            velocity.vectorY = MathUtils.clamp(velocity.vectorY + (world.getDelta() * VerticalThrusters), -VerticalMaxSpeed, VerticalMaxSpeed);
        }
        if (down)
        {
            velocity.vectorY = MathUtils.clamp(velocity.vectorY - (world.getDelta() * VerticalThrusters), -VerticalMaxSpeed, VerticalMaxSpeed);
        }

        if (left)
        {
            velocity.vectorX = MathUtils.clamp(velocity.vectorX - (world.getDelta() * HorizontalThrusters), -HorizontalMaxSpeed, HorizontalMaxSpeed);
        }
        if (right)
        {
            velocity.vectorX = MathUtils.clamp(velocity.vectorX + (world.getDelta() * HorizontalThrusters), -HorizontalMaxSpeed, HorizontalMaxSpeed);
        }*/

        if (up)
        {
            //position.y += 1.5 * world.getDelta();
        }
        if (down)
        {
            //position.y -= 1.5 * world.getDelta();
        }

        if (left)
        {
            //position.x -= 1.5 * world.getDelta();
        }
        if (right)
        {
            //position.x += 1.5 * world.getDelta();
        }

        if (shoot)
        {
            if (timeToFire <= 0)
            {
                if (mouseVector.y > position.y + 0.5f)
                    direction.y = 1;
                else
                    direction.y = -1;
                if (mouseVector.x > position.x + 0.5f)
                    direction.x = 1;
                else
                    direction.x = -1;

                Vector3 targetVector = new Vector3(mouseVector.x - position.x, mouseVector.y - position.y, 0);
                targetVector.nor(); // Normalizes vector to 1 unit

                //EntityFactory.createPlayerBullet(world, position.x, position.y, targetVector.x, targetVector.y).addToWorld();
                //EntityFactory.createPlayerBullet(world, position.x + 27, position.y + 2).addToWorld();
                timeToFire = FireRate;
            }
        }
        if (timeToFire > 0)
        {
            //timeToFire -= world.delta;
            if (timeToFire < 0)
            {
                timeToFire = 0;
            }
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.A)
        {
            left = true;
        }
        else if (keycode == Input.Keys.D)
        {
            right = true;
        }
        else if (keycode == Input.Keys.W)
        {
            up = true;
        }
        else if (keycode == Input.Keys.S)
        {
            down = true;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if (keycode == Input.Keys.A)
        {
            left = false;
        }
        else if (keycode == Input.Keys.D)
        {
            right = false;
        }
        else if (keycode == Input.Keys.W)
        {
            up = false;
        }
        else if (keycode == Input.Keys.S)
        {
            down = false;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        if (button == Input.Buttons.LEFT)
        {
            shoot = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (button == Input.Buttons.LEFT)
        {
            shoot = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }

}
