package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CrystalConstellation extends Constellation
{
    private final AutoMine autoMine;

    public CrystalConstellation(IBlockAccess world, EntityPlayer player, BlockPos pos,
                                BlockPos playerPos, IBlockState state, AutoMine autoMine)
    {
        super(world, player, pos, playerPos, state);
        this.autoMine = autoMine;
    }

    @Override
    public boolean isValid(IBlockAccess world, boolean checkPlayerState)
    {
        return super.isValid(world, checkPlayerState) && autoMine.isValidCrystalPos(pos);
    }

}
