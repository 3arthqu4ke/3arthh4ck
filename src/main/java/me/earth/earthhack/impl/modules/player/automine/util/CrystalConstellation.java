package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CrystalConstellation extends Constellation implements Globals
{
    public CrystalConstellation(IBlockAccess world, EntityPlayer player, BlockPos pos,
                                BlockPos playerPos, IBlockState state, AutoMine autoMine)
    {
        super(world, player, pos, playerPos, state, autoMine);
    }

    @Override
    public boolean isValid(IBlockAccess world, boolean checkPlayerState)
    {
        if (!autoMine.isValidCrystalPos(pos))
        {
            return false;
        }

        boolean result = superCheckNoPlayerPos(world, checkPlayerState);
        boolean correctPos = PositionUtil.getPosition(player).equals(playerPos);
        if (autoMine.damageCheck.getValue() && result && !correctPos)
        {
            IBlockStateHelper helper = new BlockStateHelper(() -> world);
            helper.addAir(pos);
            float damage = DamageUtil.calculate(pos, player, helper);
            if (damage > autoMine.minDmg.getValue())
            {
                if (autoMine.selfDmgCheck.getValue())
                {
                    float self = DamageUtil.calculate(pos, mc.player, helper);
                    return self <= autoMine.maxSelfDmg.getValue();
                }

                return true;
            }

            return false;
        }

        return result && correctPos;
    }

    protected boolean superCheckNoPlayerPos(IBlockAccess world,
                                            boolean checkPlayerState)
    {
        return world.getBlockState(pos).equals(state)
            && (!checkPlayerState
            || world.getBlockState(playerPos).equals(playerState));
    }

}
