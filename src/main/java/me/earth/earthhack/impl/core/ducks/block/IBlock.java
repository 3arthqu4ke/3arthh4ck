package me.earth.earthhack.impl.core.ducks.block;

import net.minecraft.block.state.IBlockState;

public interface IBlock
{
    void setHarvestLevelNonForge(String toolClass, int level);

    String getHarvestToolNonForge(IBlockState state);

    int getHarvestLevelNonForge(IBlockState state);

}
