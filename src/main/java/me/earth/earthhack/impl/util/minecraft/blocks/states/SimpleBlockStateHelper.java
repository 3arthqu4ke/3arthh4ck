package me.earth.earthhack.impl.util.minecraft.blocks.states;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

@SuppressWarnings("NullableProblems")
public enum SimpleBlockStateHelper implements Globals, IBlockStateHelper
{
    INSTANCE;

    @Override
    public void addBlockState(BlockPos pos, IBlockState state) { }

    @Override
    public void delete(BlockPos pos) { }

    @Override
    public void clearAllStates() { }

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
    public IBlockState getBlockState(BlockPos pos)
    {
        return mc.world.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos)
    {
        return mc.world.isAirBlock(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos)
    {
        return mc.world.getBiome(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        return mc.world.getStrongPower(pos, direction);
    }

    @Override
    public WorldType getWorldType()
    {
        return mc.world.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
    {
        return mc.world.isSideSolid(pos, side, _default);
    }
}
