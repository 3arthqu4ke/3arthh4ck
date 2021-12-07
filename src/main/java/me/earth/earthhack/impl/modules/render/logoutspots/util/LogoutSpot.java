package me.earth.earthhack.impl.modules.render.logoutspots.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class LogoutSpot implements Globals
{
    private final String name;
    private final AxisAlignedBB boundingBox;
    private final double x;
    private final double y;
    private final double z;

    public LogoutSpot(EntityPlayer player)
    {
        this.name = player.getName();
        this.boundingBox = player.getEntityBoundingBox();
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
    }

    public String getName()
    {
        return name;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public double getDistance()
    {
        return mc.player.getDistance(x, y, z);
    }

    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    public Vec3d rounded()
    {
        return new Vec3d(MathUtil.round(x, 1), MathUtil.round(y, 1), MathUtil.round(z, 1));
    }

}
