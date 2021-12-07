package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface PosPredicate
{
    boolean test(BlockPos pos);
}
