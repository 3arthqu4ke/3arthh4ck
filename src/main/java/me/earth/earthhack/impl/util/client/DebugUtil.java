package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("unused")
public class DebugUtil
{
    public static void debug(BlockPos pos, String message)
    {
        ChatUtil.sendMessage(pos.getX() + "x, "
                            + pos.getY() + "y, "
                            + pos.getZ() + "z : " + message);
    }

    public static void debug(Vec3d vec3d, String message)
    {
        ChatUtil.sendMessage(MathUtil.round(vec3d.x, 2)
                + "x, " + MathUtil.round(vec3d.y, 2)
                + "y, " + MathUtil.round(vec3d.z, 2)
                + "z : " + message);
    }

}
