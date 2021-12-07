package me.earth.earthhack.impl.modules.movement.packetfly.util;

import net.minecraft.util.math.Vec3d;

public class TimeVec extends Vec3d
{
    private final long time;

    public TimeVec(Vec3d vec3d)
    {
        this(vec3d.x, vec3d.y, vec3d.z, System.currentTimeMillis());
    }

    public TimeVec(double xIn, double yIn, double zIn, long time)
    {
        super(xIn, yIn, zIn);
        this.time = time;
    }

    public long getTime()
    {
        return time;
    }

}
