package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Map;

final class LegConstellation
{
    public final Map<BlockPos, IBlockState> states;
    public final EntityPlayer player;
    public final BlockPos targetPos;
    public final BlockPos playerPos;
    public final BlockPos firstPos;
    public final BlockPos secondPos;
    public boolean firstNeedsObby;
    public boolean secondNeedsObby;
    public boolean invalid;

    public LegConstellation(EntityPlayer player,
                            BlockPos targetPos,
                            BlockPos playerPos,
                            BlockPos firstPos,
                            BlockPos secondPos,
                            Map<BlockPos, IBlockState> states,
                            boolean firstNeedsObby,
                            boolean secondNeedsObby)
    {
        this.player          = player;
        this.targetPos       = targetPos;
        this.playerPos       = playerPos;
        this.firstPos        = firstPos;
        this.secondPos       = secondPos;
        this.states          = states;
        this.firstNeedsObby  = firstNeedsObby;
        this.secondNeedsObby = secondNeedsObby;
    }

    public boolean isValid(LegSwitch legSwitch,
                           EntityPlayer self,
                           IBlockAccess access)
    {
        if (invalid || EntityUtil.isDead(player))
        {
            return false;
        }

        if (!PositionUtil.getPosition(player).equals(playerPos)
            || !access.getBlockState(playerPos).getMaterial().isReplaceable())
        {
            return false;
        }

        if (!legSwitch.checkPos(firstPos) || !legSwitch.checkPos(secondPos))
        {
            return false;
        }

        for (Map.Entry<BlockPos, IBlockState> entry : states.entrySet())
        {
            if (!access.getBlockState(entry.getKey()).equals(entry.getValue()))
            {
                return false;
            }
        }

        float damage = DamageUtil.calculate(firstPos, self);
        if (damage > EntityUtil.getHealth(self) + 0.5
                || damage > legSwitch.maxSelfDamage.getValue())
        {
            return false;
        }

        damage = DamageUtil.calculate(secondPos, self);
        return !(damage > EntityUtil.getHealth(self) + 0.5)
                && !(damage > legSwitch.maxSelfDamage.getValue());
    }

    public void add(BlockPos pos, IBlockState state)
    {
        states.put(pos, state);
    }

}