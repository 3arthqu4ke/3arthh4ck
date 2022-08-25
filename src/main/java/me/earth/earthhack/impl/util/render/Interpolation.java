package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Interpolation implements Globals
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);

    public static Vec3d interpolatedEyePos()
    {
        return mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec()
    {
        return mc.player.getLook(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec(EntityPlayer player) {
        return player.getLook(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolateEntity(Entity entity)
    {
        double x;
        double y;
        double z;

        if (NOINTERP.isEnabled()
            && NOINTERP.get().isSilent()
            && entity instanceof IEntityNoInterp
            && ((IEntityNoInterp) entity).isNoInterping())
        {
            x = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpX(), entity.lastTickPosX)
                    - getRenderPosX();
            y = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpY(), entity.lastTickPosY)
                    - getRenderPosY();
            z = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpZ(), entity.lastTickPosZ)
                    - getRenderPosZ();
        }
        else
        {
            x = interpolateLastTickPos(entity.posX, entity.lastTickPosX)
                    - getRenderPosX();
            y = interpolateLastTickPos(entity.posY, entity.lastTickPosY)
                    - getRenderPosY();
            z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ)
                    - getRenderPosZ();
        }

        return new Vec3d(x, y, z);
    }

    public static Vec3d interpolateEntityNoRenderPos(Entity entity)
    {
        double x;
        double y;
        double z;

        if (NOINTERP.isEnabled()
                && NOINTERP.get().isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).isNoInterping())
        {
            x = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpX(), entity.lastTickPosX);
            y = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpY(), entity.lastTickPosY);
            z = interpolateLastTickPos(((IEntityNoInterp) entity).getNoInterpZ(), entity.lastTickPosZ);
        }
        else
        {
            x = interpolateLastTickPos(entity.posX, entity.lastTickPosX);
            y = interpolateLastTickPos(entity.posY, entity.lastTickPosY);
            z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ);
        }

        return new Vec3d(x, y, z);
    }

    public static Vec3d interpolateVectors(Vec3d current, Vec3d last) {
        double x = interpolateLastTickPos(current.x, last.x) - getRenderPosX();
        double y = interpolateLastTickPos(current.y, last.y) - getRenderPosY();
        double z = interpolateLastTickPos(current.z, last.z) - getRenderPosZ();
        return new Vec3d(x, y, z);
    }

    public static double interpolateLastTickPos(double pos, double lastPos)
    {
        return lastPos + (pos - lastPos) * ((IMinecraft) mc).getTimer().renderPartialTicks;
    }

    // TODO: instead of making a new AxisAlignedBB
    //  all the time maybe make a mutable AxisAlignedBB?
    public static AxisAlignedBB interpolatePos(BlockPos pos, float height)
    {
        return new AxisAlignedBB(
                pos.getX() - mc.getRenderManager().viewerPosX,
                pos.getY() - mc.getRenderManager().viewerPosY,
                pos.getZ() - mc.getRenderManager().viewerPosZ,
                pos.getX() - mc.getRenderManager().viewerPosX + 1,
                pos.getY() - mc.getRenderManager().viewerPosY + height,
                pos.getZ() - mc.getRenderManager().viewerPosZ + 1);
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb)
    {
        return new AxisAlignedBB(
                bb.minX - mc.getRenderManager().viewerPosX,
                bb.minY - mc.getRenderManager().viewerPosY,
                bb.minZ - mc.getRenderManager().viewerPosZ,
                bb.maxX - mc.getRenderManager().viewerPosX,
                bb.maxY - mc.getRenderManager().viewerPosY,
                bb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    public static void interpolateMutable(MutableBB bb)
    {
        bb.minX = bb.minX - mc.getRenderManager().viewerPosX;
        bb.minY = bb.minY - mc.getRenderManager().viewerPosY;
        bb.minZ = bb.minZ - mc.getRenderManager().viewerPosZ;
        bb.maxX = bb.maxX - mc.getRenderManager().viewerPosX;
        bb.maxY = bb.maxY - mc.getRenderManager().viewerPosY;
        bb.maxZ = bb.maxZ - mc.getRenderManager().viewerPosZ;
    }

    public static AxisAlignedBB offsetRenderPos(AxisAlignedBB bb)
    {
        return bb.offset(-getRenderPosX(), -getRenderPosY(), -getRenderPosZ());
    }

    public static double getRenderPosX()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosX();
    }

    public static double getRenderPosY()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosY();
    }

    public static double getRenderPosZ()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
    }

    public static Frustum createFrustum(Entity entity)
    {
        Frustum frustum = new Frustum();
        // NoInterp shouldn't really be required here
        setFrustum(frustum, entity);
        return frustum;
    }

    public static void setFrustum(Frustum frustum, Entity entity)
    {
        double x = interpolateLastTickPos(entity.posX, entity.lastTickPosX);
        double y = interpolateLastTickPos(entity.posY, entity.lastTickPosY);
        double z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ);

        frustum.setPosition(x, y, z);

    }

}
