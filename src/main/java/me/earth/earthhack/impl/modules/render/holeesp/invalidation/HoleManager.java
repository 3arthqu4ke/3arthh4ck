package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public interface HoleManager
{
    Map<BlockPos, Hole> getHoles();

    List<Hole> get1x1();

    List<Hole> get1x1Unsafe();

    List<Hole> get2x1();

    List<Hole> get2x2();

    default void reset()
    {
        getHoles().clear();
        get1x1().clear();
        get1x1Unsafe().clear();
        get2x1().clear();
        get2x2().clear();
    }

}
