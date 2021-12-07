package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.util.math.BlockPos;

public class SpeedmineEvent
{
    private final BlockPos pos;

    public SpeedmineEvent(BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

}
