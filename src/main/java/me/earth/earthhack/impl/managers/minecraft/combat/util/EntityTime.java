package me.earth.earthhack.impl.managers.minecraft.combat.util;

import net.minecraft.entity.Entity;

import java.util.concurrent.atomic.AtomicBoolean;

public class EntityTime
{
    private final AtomicBoolean valid = new AtomicBoolean(true);
    private final Entity entity;
    public long time;

    public EntityTime(Entity entity)
    {
        this.entity = entity;
        this.time   = System.currentTimeMillis();
    }

    public boolean passed(long ms)
    {
        return ms <= 0 || System.currentTimeMillis() - time > ms;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public void setValid(boolean valid)
    {
        this.valid.set(valid);
    }

    public boolean isValid()
    {
        return valid.get();
    }

    public void reset()
    {
        this.time = System.currentTimeMillis();
    }

}
