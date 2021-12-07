package me.earth.earthhack.impl.managers.thread.holes;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IHoleManager
{
    void setSafe(List<BlockPos> safe);

    void setUnsafe(List<BlockPos> unsafe);

    void setLongHoles(List<BlockPos> longHoles);

    void setBigHoles(List<BlockPos> bigHoles);

    void setFinished();

}
