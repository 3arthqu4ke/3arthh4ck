package me.earth.earthhack.impl.util.minecraft.blocks.states;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link IBlockAccess} that delegates all its methods
 * to <tt>mc.world</tt> except
 * {@link BlockStateHelper#getBlockState(BlockPos)}.
 * For the why read the documentation of of the
 * {@link BlockStateHelper#addBlockState(BlockPos, IBlockState)}
 * method.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class BlockStateHelper implements Globals, IBlockStateHelper
{
    private final Map<BlockPos, IBlockState> states;

    public BlockStateHelper()
    {
        this(new HashMap<>());
    }

    public BlockStateHelper(Map<BlockPos, IBlockState> stateMap)
    {
        this.states = stateMap;
    }

    /**
     * Returns an IBlockState set by
     * {@link BlockStateHelper#addBlockState(BlockPos, IBlockState)},
     * or if none was found the IBlockState from
     * {@link World#getBlockState(BlockPos)}.
     *
     * @param pos the position to get the BlockState for.
     * @return the BlockState at that Position.
     */
    @Override
    public IBlockState getBlockState(BlockPos pos)
    {
        IBlockState state = states.get(pos);
        if (state == null)
        {
            return mc.world.getBlockState(pos);
        }

        return state;
    }

    /**
     * This Method is not ThreadSafe, unless you use the second
     * constructor with a Concurrent map. If you want to use
     * this method on another Thread you are better of
     * instantiating your own BlockStateManager.
     * <p></p>
     * Some modules used to set a BlockState in the world
     * to some other BlockState, do a calculation and then
     * set it back. That doesn't go well with Multithreading
     * and other stuff, so this Manager was created.
     * <p></p>
     * Use this Method to add the given BlockState.
     * The BlockState will not be added if a BlockState
     * is already added for that position.
     * You can then use {@link BlockUtil#getFacing(BlockPos, IBlockAccess)}
     * for example.
     *
     * @param pos the position to change the BlockState at.
     * @param state the state that will be at that position.
     */
    @Override
    public void addBlockState(BlockPos pos, IBlockState state)
    {
        states.putIfAbsent(pos.toImmutable(), state);
    }

    /**
     * Removes the custom IBlockState at the given pos.
     *
     * @param pos the pos to remove.
     */
    @Override
    public void delete(BlockPos pos)
    {
        states.remove(pos);
    }

    /**
     *  Clears all BlockStates set by
     * {@link BlockStateHelper#addBlockState(BlockPos, IBlockState)}.
     */
    @Override
    public void clearAllStates()
    {
        states.clear();
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos)
    {
        return mc.world.getTileEntity(pos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        return mc.world.getCombinedLight(pos, lightValue);
    }

    @Override
    public boolean isAirBlock(BlockPos pos)
    {
        return this.getBlockState(pos)
                   .getBlock()
                   .isAir(this.getBlockState(pos), this, pos);
    }

    @Override
    public Biome getBiome(BlockPos pos)
    {
        return mc.world.getBiome(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    @Override
    public WorldType getWorldType()
    {
        return mc.world.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
    {
        if (!mc.world.isValid(pos))
        {
            return _default;
        }

        Chunk chunk = mc.world.getChunk(pos);
        //noinspection ConstantConditions
        if (chunk == null || chunk.isEmpty())
        {
            return _default;
        }

        return this.getBlockState(pos).isSideSolid(this, pos, side);
    }
}
