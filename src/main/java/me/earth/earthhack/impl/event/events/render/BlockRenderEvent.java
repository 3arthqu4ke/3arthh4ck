package me.earth.earthhack.impl.event.events.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BlockRenderEvent
{
    private final BlockPos pos;
    private final IBlockState state;

    public BlockRenderEvent(BlockPos pos, IBlockState state)
    {
        this.pos   = pos;
        this.state = state;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public IBlockState getState()
    {
        return state;
    }

}
