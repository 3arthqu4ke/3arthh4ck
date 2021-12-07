package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Performs
 * {@link IBlockProperties#collisionRayTrace(World, BlockPos, Vec3d, Vec3d)}.
 */
@FunctionalInterface
public interface CollisionFunction
{
    /**
     * {@link IBlockProperties#collisionRayTrace(World, BlockPos, Vec3d, Vec3d)}
     */
    CollisionFunction DEFAULT = IBlockProperties::collisionRayTrace;

    RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end);
}
