package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * Objects of this class represent everything
 * needed to place a block on another. To instantiate
 * this class extend it or use the functions offered
 * by the {@link RayTraceFactory}.
 */
public class Ray
{
    private final RayTraceResult result;
    private final EnumFacing facing;
    private final BlockPos pos;
    private final Vec3d vector;
    private float[] rotations;
    private boolean legit;

    public Ray(RayTraceResult result,
                  float[] rotations,
                  BlockPos pos,
                  EnumFacing facing,
                  Vec3d vector)
    {
        this.result = result;
        this.rotations = rotations;
        this.pos = pos;
        this.facing = facing;
        this.vector = vector;
    }

    public RayTraceResult getResult()
    {
        return result;
    }

    public void updateRotations(Entity entity)
    {
        if (vector != null) // TODO: what if null?
        {
            rotations = RayTraceFactory.rots(entity, vector);
        }
    }

    // TODO: These could potentially not be valid anymore...
    public float[] getRotations()
    {
        return rotations;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    /** @return <tt>true</tt> if this ray doesn't go through walls. */
    public boolean isLegit()
    {
        return legit;
    }

    public Vec3d getVector()
    {
        return vector;
    }

    /**
     * {@link Ray#isLegit()} will now return the given Value.
     *
     * @param legit the legit value.
     * @return this Ray.
     */
    public Ray setLegit(boolean legit)
    {
        this.legit = legit;
        return this;
    }

}
