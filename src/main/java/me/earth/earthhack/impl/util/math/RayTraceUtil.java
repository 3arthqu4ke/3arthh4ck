package me.earth.earthhack.impl.util.math;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

// TODO: better rayTrace for 2b2t. Find the part of the block we can see
public class RayTraceUtil implements Globals
{
    /**
     * Produces a float array of length 3 representing
     * the needed facingX, facingY and facingZ for a
     * CPacketPlayerTryUseItemOnBlock. A similar calculation
     * is made in {@link net.minecraft.client.multiplayer.PlayerControllerMP#processRightClickBlock(EntityPlayerSP, WorldClient, BlockPos, EnumFacing, Vec3d, EnumHand)}.
     *
     * @param pos the pos.
     * @param hitVec the hitVec.
     * @return facingX, facingY and facingZ.
     */
    public static float[] hitVecToPlaceVec(BlockPos pos, Vec3d hitVec)
    {
        float x = (float)(hitVec.x - pos.getX());
        float y = (float)(hitVec.y - pos.getY());
        float z = (float)(hitVec.z - pos.getZ());

        return new float[]{x, y, z};
    }

    public static RayTraceResult getRayTraceResult(float yaw, float pitch)
    {
        return getRayTraceResult(yaw, pitch, mc.playerController.getBlockReachDistance());
    }

    public static RayTraceResult getRayTraceResultWithEntity(float yaw, float pitch, Entity from)
    {
        return getRayTraceResult(yaw, pitch, mc.playerController.getBlockReachDistance(), from);
    }
    
    public static RayTraceResult getRayTraceResult(float yaw, float pitch, float distance)
    {
        return getRayTraceResult(yaw, pitch, distance, mc.player);
    }

    public static RayTraceResult getRayTraceResult(float yaw, float pitch, float d, Entity from)
    {
        Vec3d vec3d     = PositionUtil.getEyePos(from);
        Vec3d lookVec   = RotationUtil.getVec3d(yaw, pitch);
        Vec3d rotations = vec3d.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        return Optional.ofNullable(
            mc.world.rayTraceBlocks(vec3d, rotations, false, false, false))
                      .orElseGet(() ->
          new RayTraceResult(RayTraceResult.Type.MISS,
                  new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN));
    }

    public static boolean canBeSeen(double x, double y, double z, Entity by)
    {
        return canBeSeen(new Vec3d(x, y, z), by.posX, by.posY, by.posZ, by.getEyeHeight());
    }

    public static boolean canBeSeen(Vec3d toSee, Entity by)
    {
        return canBeSeen(toSee, by.posX, by.posY, by.posZ, by.getEyeHeight());
    }

    public static boolean canBeSeen(Vec3d toSee, double x, double y, double z, float eyeHeight)
    {
        Vec3d start = new Vec3d(x, y + eyeHeight, z);
        return mc.world.rayTraceBlocks(start, toSee, false, true, false) == null;
    }

    public static boolean canBeSeen(Entity toSee, EntityLivingBase by)
    {
        return by.canEntityBeSeen(toSee);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean raytracePlaceCheck(Entity entity, BlockPos pos)
    {
        return getFacing(entity, pos, false) != null;
    }

    public static EnumFacing getFacing(Entity entity,
                                       BlockPos pos,
                                       boolean verticals)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            RayTraceResult result = mc.world.rayTraceBlocks(
             PositionUtil.getEyePos(entity),
             new Vec3d(
                pos.getX() + 0.5 + facing.getDirectionVec().getX() * 1.0 / 2.0,
                pos.getY() + 0.5 + facing.getDirectionVec().getY() * 1.0 / 2.0,
                pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 1.0 / 2.0),
             false,
             true,
             false);

            if (result != null
                    && result.typeOfHit == RayTraceResult.Type.BLOCK
                    && result.getBlockPos().equals(pos))
            {
                return facing;
            }
        }

        if (verticals)
        {
            if (pos.getY() > mc.player.posY + mc.player.getEyeHeight())
            {
                return EnumFacing.DOWN;
            }

            return EnumFacing.UP;
        }

        return null;
    }

}
