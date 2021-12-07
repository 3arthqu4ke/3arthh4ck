package me.earth.earthhack.impl.modules.combat.antisurround.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface AntiSurroundFunction
{
    void accept(BlockPos pos,
                BlockPos down,
                BlockPos on,
                EnumFacing onFacing,
                int obbySlot,
                MineSlots slots,
                int crystalSlot,
                Entity blocking,
                EntityPlayer found,
                boolean execute);
}