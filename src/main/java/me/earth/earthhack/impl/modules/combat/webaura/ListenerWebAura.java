package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class ListenerWebAura extends NoAttackObbyListener<WebAura>
{
    public ListenerWebAura(WebAura module)
    {
        super(module, -10);
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        switch (module.target.getValue())
        {
            case Closest:
                module.currentTarget = EntityUtil.getClosestEnemy();
                if (module.currentTarget == null
                        || module.currentTarget.getDistanceSq(mc.player)
                            > MathUtil.square(module.targetRange.getValue()))
                {
                    return result.setValid(false);
                }

                return trap(module.currentTarget, result);
            case Untrapped:
                module.currentTarget = null;
                List<EntityPlayer> players = new ArrayList<>();
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (player == null
                            || EntityUtil.isDead(player)
                            || Managers.FRIENDS.contains(player)
                            || player.equals(mc.player))
                    {
                        continue;
                    }

                    BlockPos pos = new BlockPos(player);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.WEB
                            || mc.world.getBlockState(pos.up())
                                       .getBlock() == Blocks.WEB)
                    {
                        continue;
                    }

                    if (mc.player.getDistanceSq(player)
                            < MathUtil.square(module.targetRange.getValue()))
                    {
                        players.add(player);
                    }
                }

                players.sort(Comparator.comparingDouble(p ->
                        p.getDistanceSq(mc.player)));

                for (EntityPlayer player : players)
                {
                    trap(player, result);
                }

                return result;
            default:
                return result.setValid(false);
        }
    }

    @Override
    protected int getSlot()
    {
        return InventoryUtil.findHotbarBlock(Blocks.WEB);
    }

    @Override
    protected String getDisableString()
    {
        return "Disabled, no Webs.";
    }

    private TargetResult trap(Entity entity, TargetResult result)
    {
        BlockPos pos = new BlockPos(entity);
        BlockPos up  = pos.up();
        IBlockState state   = mc.world.getBlockState(pos);
        IBlockState upState = mc.world.getBlockState(up);

        if (state.getBlock() == Blocks.WEB
                || upState.getBlock() == Blocks.WEB)
        {
            return result;
        }

        if (state.getMaterial().isReplaceable())
        {
            result.getTargets().add(pos);
        }
        else if (upState.getMaterial().isReplaceable())
        {
            result.getTargets().add(up);
        }

        return result;
    }

}
