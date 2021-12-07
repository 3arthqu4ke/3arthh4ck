package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public class BBUtil
{
    public static boolean intersects(AxisAlignedBB bb, Vec3i vec3i)
    {
        return bb.minX < vec3i.getX() + 1
                && bb.maxX > vec3i.getX()
                && bb.minY < vec3i.getY() + 1
                && bb.maxY > vec3i.getY()
                && bb.minZ < vec3i.getZ() + 1
                && bb.maxZ > vec3i.getZ();
    }

}
