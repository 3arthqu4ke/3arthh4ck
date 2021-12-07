package me.earth.earthhack.impl.util.helpers.render;

import net.minecraft.util.math.AxisAlignedBB;

/** {@link BlockESPBuilder} */
@FunctionalInterface
public interface IAxisESP
{
    /** Draws a BlockESP at the given AxisAlignedBB. */
    void render(AxisAlignedBB bb);

}
