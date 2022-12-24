package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ClickBlockEvent extends Event
{
    private final BlockPos pos;
    private final EnumFacing facing;

    public ClickBlockEvent(BlockPos pos, EnumFacing facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public EnumFacing getFacing()
    {
        return facing;
    }

    public static class Right extends ClickBlockEvent
    {
        private final Vec3d vec;
        private final EnumHand hand;

        public Right(BlockPos pos, EnumFacing facing, Vec3d vec, EnumHand hand)
        {
            super(pos, facing);
            this.vec = vec;
            this.hand = hand;
        }

        public EnumHand getHand()
        {
            return hand;
        }

        public Vec3d getVec()
        {
            return vec;
        }
    }
}
