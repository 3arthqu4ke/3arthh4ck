package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.MoverType;

public class MoveEvent extends Event
{
    private final MoverType type;
    private double x;
    private double y;
    private double z;
    private boolean sneaking;

    public MoveEvent(MoverType type, double x, double y, double z, boolean sneaking)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sneaking = sneaking;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public MoverType getType()
    {
        return type;
    }

    public boolean isSneaking()
    {
        return sneaking;
    }

    public void setSneaking(boolean sneaking)
    {
        this.sneaking = sneaking;
    }

}
