package org.rabus.ProjectOne.components;

import com.apollo.Component;

public class Health extends Component
{
    private int maximumHealth;
    private float currentHealth;
    private boolean killed;

    public Health(int maximumHealth)
    {
        this(maximumHealth, maximumHealth);
    }

    public Health(int currentHealth, int maximumHealth)
    {
        this.currentHealth = currentHealth;
        this.maximumHealth = maximumHealth;
    }

    @Override
    public Class<? extends Component> getType()
    {
        return Health.class;
    }

    @Override
    public void update(int delta)
    {
        if (killed)
        {
            owner.fireEvent("KILLED");
        }
    }

    public void addDamage(float damage)
    {
        setCurrentHealth(currentHealth - damage);
    }

    public int getMaximumHealth()
    {
        return maximumHealth;
    }

    public void setCurrentHealth(float currentHealth)
    {
        this.currentHealth = currentHealth;

        if (this.currentHealth > maximumHealth)
        {
            this.currentHealth = maximumHealth;
        }
        else if (this.currentHealth <= 0)
        {
            this.currentHealth = 0;
            killed = true;
        }
    }

    public float getHealthStatus()
    {
        return (float) currentHealth / (float) maximumHealth;
    }

    public void kill()
    {
        currentHealth = 0;
        killed = true;
    }

    public float getCurrentHealth()
    {
        return currentHealth;
    }

    public boolean isKilled()
    {
        return killed;
    }

    public void addHealth(float health)
    {
        currentHealth += health;
        if (currentHealth > maximumHealth)
            currentHealth = maximumHealth;
    }

    public boolean isAtFullHealth()
    {
        return currentHealth == maximumHealth;
    }

}
