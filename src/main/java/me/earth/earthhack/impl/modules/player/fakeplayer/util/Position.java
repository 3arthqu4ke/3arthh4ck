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
    private final double motionX;
    private final double motionY;
    private final double motionZ;

    public Position(EntityPlayer player)
    {
        this.x       = player.posX;
        this.y       = player.posY;
        this.z       = player.posZ;
        this.yaw     = player.rotationYaw;
        this.pitch   = player.rotationPitch;
        this.head    = player.rotationYawHead;
        this.motionX = player.motionX;
        this.motionY = player.motionY;
        this.motionZ = player.motionZ;
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

    public double getMotionX()
    {
        return motionX;
    }

    public double getMotionY()
    {
        return motionY;
    }

    public double getMotionZ()
    {
        return motionZ;
    }

}