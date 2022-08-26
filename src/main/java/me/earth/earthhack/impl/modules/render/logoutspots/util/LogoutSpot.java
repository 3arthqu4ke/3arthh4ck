package me.earth.earthhack.impl.modules.render.logoutspots.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class LogoutSpot extends TimeStamp implements Globals
{
    private final String name;
    private final StaticModelPlayer model;
    private final AxisAlignedBB boundingBox;
    private final double x;
    private final double y;
    private final double z;

    public LogoutSpot(EntityPlayer player)
    {
        this.name = player.getName();
        this.model = new StaticModelPlayer(PlayerUtil.copyPlayer(player),
                player instanceof AbstractClientPlayer && ((AbstractClientPlayer)player).getSkinType().equals("slim"),
                0);
        this.model.disableArmorLayers();
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

    public StaticModelPlayer getModel() {
        return model;
    }

    public Vec3d rounded()
    {
        return new Vec3d(MathUtil.round(x, 1), MathUtil.round(y, 1), MathUtil.round(z, 1));
    }

}
