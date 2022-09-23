package me.earth.earthhack.impl.util.math.position;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PositionUtil implements Globals
{
    public static BlockPos getPosition()
    {
        //return getPosition(mc.player);
        return getPosition(RotationUtil.getRotationPlayer());
    }

    public static BlockPos getPosition(Entity entity)
    {
        return getPosition(entity, 0.0);
    }

    public static BlockPos getPosition(Entity entity, double yOffset)
    {
        double y = entity.posY + yOffset;
        if (entity.posY - Math.floor(entity.posY) > 0.5)
        {
            y = Math.ceil(entity.posY);
        }

        return new BlockPos(entity.posX, y, entity.posZ);
    }

    public static Vec3d getEyePos()
    {
        return getEyePos(mc.player);
    }

    public static Vec3d getEyePos(Entity entity)
    {
        return new Vec3d(entity.posX, getEyeHeight(entity), entity.posZ);
    }

    public static double getEyeHeight()
    {
        return getEyeHeight(mc.player);
    }

    public static double getEyeHeight(Entity entity)
    {
        return entity.posY + entity.getEyeHeight();
    }

    public static Set<BlockPos> getBlockedPositions(Entity entity)
    {
        return getBlockedPositions(entity.getEntityBoundingBox());
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb)
    {
        return getBlockedPositions(bb, 0.5);
    }

    public static Set<BlockPos> getBlockedPositions(AxisAlignedBB bb,
                                                    double offset)
    {
        Set<BlockPos> positions = new HashSet<>();

        double y = bb.minY;
        if (bb.minY - Math.floor(bb.minY) > offset)
        {
            y = Math.ceil(bb.minY);
        }

        positions.add(new BlockPos(bb.maxX, y, bb.maxZ));
        positions.add(new BlockPos(bb.minX, y, bb.minZ));
        positions.add(new BlockPos(bb.maxX, y, bb.minZ));
        positions.add(new BlockPos(bb.minX, y, bb.maxZ));

        return positions;
    }

    public static boolean isBoxColliding()
    {
        return mc.world.getCollisionBoxes(mc.player,
                                          mc.player
                                            .getEntityBoundingBox()
                                            .offset(0.0, 0.21, 0.0))
                                            .size() > 0;
    }

    public static Entity getPositionEntity()
    {
        EntityPlayerSP player = mc.player;
        Entity ridingEntity;
        return player == null
                ? null
                : (ridingEntity = player.getRidingEntity()) != null
                    && !(ridingEntity instanceof EntityBoat)
                    ? ridingEntity
                    : player;
    }

    public static Entity requirePositionEntity()
    {
        return Objects.requireNonNull(getPositionEntity());
    }

    public static boolean inLiquid()
    {
        return inLiquid(MathHelper.floor(
            requirePositionEntity().getEntityBoundingBox().minY + 0.01));
    }

    public static boolean inLiquid(boolean feet)
    {
        return inLiquid(MathHelper.floor(
            requirePositionEntity().getEntityBoundingBox().minY
                - (feet ? 0.03 : 0.2)));
    }

    private static boolean inLiquid(int y)
    {
        return findState(BlockLiquid.class, y) != null;
    }

    private static IBlockState findState(Class<? extends Block> block, int y)
    {
        Entity entity = requirePositionEntity();
        int startX = MathHelper.floor(entity.getEntityBoundingBox().minX);
        int startZ = MathHelper.floor(entity.getEntityBoundingBox().minZ);
        int endX   = MathHelper.ceil(entity.getEntityBoundingBox().maxX);
        int endZ   = MathHelper.ceil(entity.getEntityBoundingBox().maxZ);
        for (int x = startX; x < endX; x++)
        {
            for (int z = startZ; z < endZ; z++)
            {
                IBlockState s = mc.world.getBlockState(new BlockPos(x, y, z));
                if (block.isInstance(s.getBlock()))
                {
                    return s;
                }
            }
        }

        return null;
    }

    public static boolean isMovementBlocked()
    {
        IBlockState state = findState(Block.class,
              MathHelper.floor(mc.player.getEntityBoundingBox().minY - 0.01));
        return state != null  && state.getMaterial().blocksMovement();
    }

    public static boolean isAbove(BlockPos pos)
    {
        return mc.player.getEntityBoundingBox().minY >= pos.getY();
    }

    public static BlockPos fromBB(AxisAlignedBB bb)
    {
        return new BlockPos((bb.minX + bb.maxX) / 2.0,
                            (bb.minY + bb.maxY) / 2.0,
                            (bb.minZ + bb.maxZ) / 2.0);
    }

    public static boolean intersects(AxisAlignedBB bb, BlockPos pos)
    {
        return bb.intersects(pos.getX(),
                             pos.getY(),
                             pos.getZ(),
                             pos.getX() + 1,
                             pos.getY() + 1,
                             pos.getZ() + 1);
    }

}
