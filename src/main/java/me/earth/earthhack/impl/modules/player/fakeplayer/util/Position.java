package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import net.minecraft.entity.player.EntityPlayer;

public class Position
{
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final float head;

    public Position(EntityPlayer player)
    {
        this.x     = player.posX;
        this.y     = player.posY;
        this.z     = player.posZ;
        this.yaw   = player.rotationYaw;
        this.pitch = player.rotationPitch;
        this.head  = player.rotationYawHead;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getHead()
    {
        return head;
    }

}