package me.earth.earthhack.impl.util.math.raytrace;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.EnumFacing.*;

/**
 * Smart Raytracing.
 * It's recommended to use a resolution of -1.0 whenever possible.
 * TODO: Use Resolution for Offsets to the corners
 */
public class RayTraceFactory implements Globals
{
    // TODO: better facing sorting,
    // TODO: only apply impossible facings if dumbRay is null
    private static final EnumFacing[] T = {UP, NORTH, SOUTH, WEST, EAST, DOWN};
    private static final EnumFacing[] B = {DOWN, NORTH, SOUTH, WEST, EAST, UP};
    private static final EnumFacing[] S = {EAST, NORTH, SOUTH, WEST, UP, DOWN};

    private RayTraceFactory() { throw new AssertionError(); }

    /**
     * Tries to find suitable BlockPositions around
     * the given one, which we can raytrace to, using
     * {@link RayTraceFactory#rayTrace(
     * Entity, BlockPos, EnumFacing, IBlockAccess, IBlockState, double)}.
     *
     * @param from the entity from whose eyes to raytrace.
     * @param world the IBlockAccess supplying the IBlockStates.
     * @param pos the position.
     * @param resolution the resolution (-1.0 is recommended).
     * @return a Ray. (might be null)
     */
    public static Ray fullTrace(Entity from,
                                IBlockAccess world,
                                BlockPos pos,
                                double resolution)
    {
        Ray dumbRay = null;
        double closest = Double.MAX_VALUE;
        for (EnumFacing facing : getOptimalFacings(from, pos))
        {
            BlockPos offset = pos.offset(facing);
            IBlockState state = world.getBlockState(offset);
            if (state.getMaterial().isReplaceable())
            {
                continue;
            }

            Ray ray = rayTrace(
                  from, offset, facing.getOpposite(), world, state, resolution);
            if (ray.isLegit())
            {
                return ray;
            }

            double dist = BlockUtil.getDistanceSq(from, offset);
            if (dumbRay == null || dist < closest)
            {
                closest = dist;
                dumbRay = ray;
            }
        }

        return dumbRay;
    }

    /**
     * Performs the SmartRaytrace.
     * A res value of >= 1.0 means that just the middle
     * of the block will be traced, a value <= 0 means
     * that the 4 corners and the middle will be traced
     * and other values set the step width for many raytraces.
     * A value of -1.0 is recommended.
     *
     * @param from the entity from whose eyes to trace.
     * @param on the position to trace to.
     * @param facing the offset to the position.
     * @param access BlockAccess
     * @param state the state at the position
     * @param res the resolution as explained above
     * @return a Ray, never null.
     */
    public static Ray rayTrace(Entity from,
                               BlockPos on,
                               EnumFacing facing,
                               IBlockAccess access,
                               IBlockState state,
                               double res)
    {
        Vec3d start = PositionUtil.getEyePos(from);
        AxisAlignedBB bb = state.getBoundingBox(access, on);
        if (res >= 1.0)
        {
            float[] r = rots(on, facing, from, access, state);
            Vec3d look = RotationUtil.getVec3d(r[0], r[1]);
            double d = mc.playerController.getBlockReachDistance();
            Vec3d rotations = start.add(look.x * d, look.y * d, look.z * d);
            RayTraceResult result = RayTracer.trace(mc.world,
                                                    access,
                                                    start,
                                                    rotations,
                                                    false,
                                                    false,
                                                    true);
            if (result == null
                || result.sideHit != facing
                || !on.equals(result.getBlockPos()))
            {
                return dumbRay(on, facing, r);
            }

            return new Ray(result, r, on, facing, null).setLegit(true);
        }
        else
        {
            Vec3i dirVec = facing.getDirectionVec();
            double dirX = dirVec.getX() < 0
                    ? bb.minX
                    : dirVec.getX() * bb.maxX;
            double dirY = dirVec.getY() < 0
                    ? bb.minY
                    : dirVec.getY() * bb.maxY;
            double dirZ = dirVec.getZ() < 0
                    ? bb.minZ
                    : dirVec.getZ() * bb.maxZ;

            double minX = on.getX() + dirX
                                    + (dirVec.getX() == 0 ? bb.minX : 0);
            double minY = on.getY() + dirY
                                    + (dirVec.getY() == 0 ? bb.minY : 0);
            double minZ = on.getZ() + dirZ
                                    + (dirVec.getZ() == 0 ? bb.minZ : 0);

            double maxX = on.getX() + dirX
                                    + (dirVec.getX() == 0 ? bb.maxX : 0);
            double maxY = on.getY() + dirY
                                    + (dirVec.getY() == 0 ? bb.maxY : 0);
            double maxZ = on.getZ() + dirZ
                                    + (dirVec.getZ() == 0 ? bb.maxZ : 0);

            boolean xEq = Double.compare(minX, maxX) == 0;
            boolean yEq = Double.compare(minY, maxY) == 0;
            boolean zEq = Double.compare(minZ, maxZ) == 0;

            // These ifs set the position slightly into the block
            if (xEq)
            {
                minX -= dirVec.getX() * 0.0005;
                maxX = minX;
            }

            if (yEq)
            {
                minY -= dirVec.getY() * 0.0005;
                maxY = minY;
            }

            if (zEq)
            {
                minZ -= dirVec.getZ() * 0.0005;
                maxZ = minZ;
            }

            // determine max and min x, y and z
            // xEq ? 0 : 0.0005 makes slight offsets or we hit other blocks
            // TODO: use the Res to make the Offsets!!!
            double endX = Math.max(minX, maxX) - (xEq ? 0 : 0.0005);
            double endY = Math.max(minY, maxY) - (yEq ? 0 : 0.0005);
            double endZ = Math.max(minZ, maxZ) - (zEq ? 0 : 0.0005);

            if (res <= 0.0) // 4 corners + middle
            {
                double staX = Math.min(minX, maxX) + (xEq ? 0 : 0.0005);
                double staY = Math.min(minY, maxY) + (yEq ? 0 : 0.0005);
                double staZ = Math.min(minZ, maxZ) + (zEq ? 0 : 0.0005);

                // I mean instead of using a Set we could just think
                // about which 5 vectors are unique but ¯\_(ツ)_/¯
                Set<Vec3d> vectors = new HashSet<>();
                vectors.add(new Vec3d(staX, staY, staZ));
                vectors.add(new Vec3d(staX, staY, endZ));
                vectors.add(new Vec3d(staX, endY, staZ));
                vectors.add(new Vec3d(staX, endY, endZ));
                vectors.add(new Vec3d(endX, staY, staZ));
                vectors.add(new Vec3d(endX, staY, endZ));
                vectors.add(new Vec3d(endX, endY, staZ));
                vectors.add(new Vec3d(endX, endY, endZ));

                double x = (endX - staX) / 2.0 + staX;
                double y = (endY - staY) / 2.0 + staY;
                double z = (endZ - staZ) / 2.0 + staZ;
                // middle of the block side
                vectors.add(new Vec3d(x, y, z));

                for (Vec3d vec : vectors)
                {
                    RayTraceResult ray = RayTracer.trace(
                            mc.world, access, start, vec, false, false, true);

                    if (ray != null
                            && on.equals(ray.getBlockPos())
                            && facing == ray.sideHit)
                    {
                        return new Ray(ray, rots(from, vec), on, facing, vec)
                                .setLegit(true);
                    }
                }

                return dumbRay(
                        on, facing, rots(on, facing, from, access, state));
            }

            // TODO: this shouldn't be required anymore
            for (double x = Math.min(minX, maxX); x <= endX; x += res)
            {
                for (double y = Math.min(minY, maxY); y <= endY; y += res)
                {
                    for (double z = Math.min(minZ, maxZ); z <= endZ; z += res)
                    {
                        Vec3d vector = new Vec3d(x, y, z);
                        RayTraceResult ray = RayTracer.trace(
                           mc.world, access, start, vector, false, false, true);
                        if (ray != null
                                && facing == ray.sideHit
                                && on.equals(ray.getBlockPos()))
                        {
                            return new Ray(ray, rots(from, vector), on, facing, vector)
                                    .setLegit(true);
                        }
                    }
                }
            }
        }

        return dumbRay(on, facing, rots(on, facing, from, access, state));
    }

    public static Ray dumbRay(BlockPos on, EnumFacing offset, float[] rotations)
    {
        return newRay(new RayTraceResult(RayTraceResult.Type.MISS,
                                          new Vec3d(0.5, 1.0, 0.5),
                                          UP,
                                          BlockPos.ORIGIN),
                      on,
                      offset,
                      rotations);
    }

    public static Ray newRay(RayTraceResult result,
                             BlockPos on,
                             EnumFacing offset,
                             float[] rotations)
    {
        return new Ray(result, rotations, on, offset, null);
    }

    /*------------- Util -------------*/

    static float[] rots(Entity from, Vec3d vec3d)
    {
        return RotationUtil.getRotations(vec3d.x, vec3d.y, vec3d.z, from);
    }

    private static float[] rots(BlockPos pos,
                                EnumFacing facing,
                                Entity from,
                                IBlockAccess world,
                                IBlockState state)
    {
        return RotationUtil.getRotations(pos, facing, from, world, state);
    }

    private static EnumFacing[] getOptimalFacings(Entity player, BlockPos pos)
    {
        if (pos.getY() > player.posY + 2)
        {
            return T;
        }
        else if (pos.getY() < player.posY)
        {
            return B;
        }

        return S;
    }

}
