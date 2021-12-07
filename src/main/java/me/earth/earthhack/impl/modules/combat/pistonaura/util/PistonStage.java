package me.earth.earthhack.impl.modules.combat.pistonaura.util;

import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public enum PistonStage
{
    CRYSTAL(PistonData::getCrystalPos),
    PISTON(PistonData::getPistonPos),
    REDSTONE(PistonData::getRedstonePos),
    BREAK(data -> data.getCrystalPos().up());

    private final Function<PistonData, BlockPos> function;

    PistonStage(Function<PistonData, BlockPos> function)
    {
        this.function = function;
    }

    public BlockPos getPos(PistonData data)
    {
        return function.apply(data);
    }

}
