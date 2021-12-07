package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class DamageBlockEvent extends Event
{
    private final BlockPos pos;
    private final EnumFacing facing;
    private float damage;
    private int delay;

    public DamageBlockEvent(BlockPos pos, EnumFacing facing, float damage, int delay)
    {
        this.pos = pos;
        this.facing = facing;
        this.damage = damage;
        this.delay = delay;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public float getDamage()
    {
        return damage;
    }

    public void setDamage(float damage)
    {
        this.damage = damage;
    }

    public int getDelay()
    {
        return delay;
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

}
