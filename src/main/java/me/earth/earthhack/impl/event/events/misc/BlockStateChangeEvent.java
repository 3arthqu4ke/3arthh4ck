package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BlockStateChangeEvent
{
    private final BlockPos pos;
    private final IBlockState state;
    private final IChunk chunk;

    public BlockStateChangeEvent(BlockPos pos, IBlockState state, IChunk chunk)
    {
        this.pos = pos;
        this.state = state;
        this.chunk = chunk;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public IBlockState getState()
    {
        return state;
    }

    public IChunk getChunk()
    {
        return chunk;
    }
}
