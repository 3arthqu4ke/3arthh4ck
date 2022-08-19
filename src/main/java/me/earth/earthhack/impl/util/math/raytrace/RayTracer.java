package me.earth.earthhack.impl.util.math.raytrace;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import me.earth.earthhack.impl.managers.minecraft.movement.RotationManager;
import me.earth.earthhack.impl.util.math.path.TriPredicate;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Better Raytracing.
 * (Custom EntityRaytrace, IBlockAccess, Predicates)
 */
@SuppressWarnings("Guava")
public class RayTracer implements Globals
{
    private static final Predicate<Entity> PREDICATE =
        Predicates.and(EntitySelectors.NOT_SPECTATING,
                       e -> e != null && e.canBeCollidedWith());

    public static RayTraceResult rayTraceEntities(World world,
                                                  Entity from,
                                                  double range,
                                                  PositionManager position,
                                                  RotationManager rotation,
                                                  Predicate<Entity> entityCheck,
                                                  Entity...additional)
    {
        return rayTraceEntities(world,
                                from,
                                range,
                                position.getX(),
                                position.getY(),
                                position.getZ(),
                                rotation.getServerYaw(),
                                rotation.getServerPitch(),
                                position.getBB(),
                                entityCheck,
                                additional);
    }

    public static RayTraceResult rayTraceEntities(World world,
                                                  Entity from,
                                                  double range,
                                                  double posX,
                                                  double posY,
                                                  double posZ,
                                                  float yaw,
                                                  float pitch,
                                                  AxisAlignedBB fromBB,
                                                  Predicate<Entity> entityCheck,
                                                  Entity...additional)
    {
        Vec3d eyePos =
                new Vec3d(posX, posY + from.getEyeHeight(), posZ);
        Vec3d rot =
                RotationUtil.getVec3d(yaw, pitch);
        Vec3d intercept =
                eyePos.add(rot.x * range, rot.y * range, rot.z * range);

        Entity pointedEntity = null;
        Vec3d hitVec = null;
        double distance = range;

        List<Entity> entities;
        AxisAlignedBB within = fromBB.expand(rot.x * range,
                                           rot.y * range,
                                           rot.z * range)
                                     .grow(1.0, 1.0, 1.0);

        Predicate<Entity> predicate = entityCheck == null
                ? PREDICATE
                : Predicates.and(PREDICATE, entityCheck);

        if (mc.isCallingFromMinecraftThread())
        {
            entities = world.getEntitiesInAABBexcluding(from,
                                                        within,
                                                        predicate);
        }
        else
        {
            entities = Managers.ENTITIES
                               .getEntities()
                               .stream()
                               .filter(e -> e != null
                                        && e.getEntityBoundingBox()
                                            .intersects(within)
                                        && predicate.test(e))
                              .collect(Collectors.toList());
        }

        for (Entity entity : additional)
        {
            if (entity != null
                    && entity.getEntityBoundingBox().intersects(within))
            {
                entities.add(entity);
            }
        }

        for (Entity entity : entities)
        {
            AxisAlignedBB bb = entity.getEntityBoundingBox()
                                     .grow(entity.getCollisionBorderSize());

            RayTraceResult result = bb.calculateIntercept(eyePos, intercept);

            if (bb.contains(eyePos))
            {
                if (distance >= 0.0)
                {
                    pointedEntity = entity;
                    hitVec = result == null ? eyePos : result.hitVec;
                    distance = 0.0;
                }
            }
            else if (result != null)
            {
                double hitDistance = eyePos.distanceTo(result.hitVec);

                if (hitDistance < distance || distance == 0.0)
                {
                    if (entity.getLowestRidingEntity()
                            == from.getLowestRidingEntity())
                        // TODO: && !entity.canRiderInteract()) for Vanilla?
                    {
                        if (distance == 0.0)
                        {
                            pointedEntity = entity;
                            hitVec = result.hitVec;
                        }
                    }
                    else
                    {
                        pointedEntity = entity;
                        hitVec = result.hitVec;
                        distance = hitDistance;
                    }
                }
            }
        }

        if (pointedEntity != null && hitVec != null)
        {
            return new RayTraceResult(pointedEntity, hitVec);
        }

        return null;
    }

    /**
     * Calls {@link RayTracer#trace(World, IBlockAccess, Vec3d,
     * Vec3d, boolean, boolean, boolean, BiPredicate)} for no Predicate.
     */
    public static RayTraceResult trace(World world,
                                       IBlockAccess access,
                                       Vec3d start,
                                       Vec3d end,
                                       boolean stopOnLiquid,
                                       boolean ignoreBlockWithoutBoundingBox,
                                       boolean returnLastUncollidableBlock)
    {
        return trace(world,
                     access,
                     start,
                     end,
                     stopOnLiquid,
                     ignoreBlockWithoutBoundingBox,
                     returnLastUncollidableBlock,
                     null);
    }

    /**
     * Calls {@link RayTracer#trace(World, IBlockAccess, Vec3d, Vec3d,
     * boolean, boolean, boolean, BiPredicate)} for the world as access.
     */
    public static RayTraceResult trace(World world,
                                       Vec3d start,
                                       Vec3d end,
                                       boolean stopOnLiquid,
                                       boolean ignoreBlockWithoutBoundingBox,
                                       boolean returnLastUncollidableBlock,
                                       BiPredicate<Block, BlockPos> blockChecker)
    {
        return trace(world,
                     world,
                     start,
                     end,
                     stopOnLiquid,
                     ignoreBlockWithoutBoundingBox,
                     returnLastUncollidableBlock,
                     blockChecker);
    }

    /**
     * {@link World#rayTraceBlocks(Vec3d, Vec3d, boolean, boolean, boolean)}.
     * But allows you to use a custom {@link IBlockAccess}.
     *
     * @param world used for
     * {@link IBlockState#collisionRayTrace(World, BlockPos, Vec3d, Vec3d)}.
     * @param access gets the IBlockStates.
     * @param start the start.
     * @param end the end.
     * @param stopOnLiquid stops on liquids.
     * @param ignoreBlockWithoutBoundingBox ignores blocks without a BB.
     * @param returnLastUncollidableBlock returns last uncollidable block.
     * @return
     * {@link World#rayTraceBlocks(Vec3d, Vec3d, boolean, boolean, boolean)}.
     */
    public static RayTraceResult trace(World world,
                                       IBlockAccess access,
                                       Vec3d start,
                                       Vec3d end,
                                       boolean stopOnLiquid,
                                       boolean ignoreBlockWithoutBoundingBox,
                                       boolean returnLastUncollidableBlock,
                                       BiPredicate<Block, BlockPos> blockChecker)
    {
        return traceTri(world,
                        access,
                        start,
                        end,
                        stopOnLiquid,
                        ignoreBlockWithoutBoundingBox,
                        returnLastUncollidableBlock,
                        blockChecker == null
                                ? null
                                : (b,p,ef) -> blockChecker.test(b,p));
    }

    public static RayTraceResult traceTri(World world,
                                          IBlockAccess access,
                                          Vec3d start,
                                          Vec3d end,
                                          boolean stopOnLiquid,
                                          boolean ignoreBlockWithoutBoundingBox,
                                          boolean returnLastUncollidableBlock,
                                          TriPredicate<Block, BlockPos, EnumFacing> blockChecker)
    {
        return traceTri(world,
                        access,
                        start,
                        end,
                        stopOnLiquid,
                        ignoreBlockWithoutBoundingBox,
                        returnLastUncollidableBlock,
                        blockChecker,
                        null);
    }

    public static RayTraceResult traceTri(World world,
                                          IBlockAccess access,
                                          Vec3d start,
                                          Vec3d end,
                                          boolean stopOnLiquid,
                                          boolean ignoreBlockWithoutBoundingBox,
                                          boolean returnLastUncollidableBlock,
                                          TriPredicate<Block, BlockPos, EnumFacing> blockChecker,
                                          TriPredicate<Block, BlockPos, EnumFacing> collideCheck)
    {
        return traceTri(world,
                        access,
                        start,
                        end,
                        stopOnLiquid,
                        ignoreBlockWithoutBoundingBox,
                        returnLastUncollidableBlock,
                        blockChecker,
                        collideCheck,
                        CollisionFunction.DEFAULT);
    }

    public static RayTraceResult traceTri(World world,
                                          IBlockAccess access,
                                          Vec3d start,
                                          Vec3d end,
                                          boolean stopOnLiquid,
                                          boolean ignoreBlockWithoutBoundingBox,
                                          boolean returnLastUncollidableBlock,
                                          TriPredicate<Block, BlockPos, EnumFacing> blockChecker,
                                          TriPredicate<Block, BlockPos, EnumFacing> collideCheck,
                                          CollisionFunction crt)
    {
        if (!Double.isNaN(start.x)
                && !Double.isNaN(start.y)
                && !Double.isNaN(start.z))
        {
            if (!Double.isNaN(end.x)
                    && !Double.isNaN(end.y)
                    && !Double.isNaN(end.z))
            {
                int feX = MathHelper.floor(end.x);
                int feY = MathHelper.floor(end.y);
                int feZ = MathHelper.floor(end.z);
                int fsX = MathHelper.floor(start.x);
                int fsY = MathHelper.floor(start.y);
                int fsZ = MathHelper.floor(start.z);
                BlockPos pos = new BlockPos(fsX, fsY, fsZ);
                IBlockState state = access.getBlockState(pos);
                Block block = state.getBlock();

                if ((!ignoreBlockWithoutBoundingBox
                    || state.getCollisionBoundingBox(access, pos)
                        != Block.NULL_AABB)
                    && (block.canCollideCheck(state, stopOnLiquid)
                        || collideCheck != null
                            && collideCheck.test(block, pos, null))
                    && (blockChecker == null
                            || blockChecker.test(block, pos, null)))
                {
                    RayTraceResult raytraceresult =
                        crt.collisionRayTrace(state, world, pos, start, end);

                    if (raytraceresult != null)
                    {
                        return raytraceresult;
                    }
                }

                RayTraceResult result = null;
                int steps = 200;

                while (steps-- >= 0)
                {
                    if (Double.isNaN(start.x)
                            || Double.isNaN(start.y)
                            || Double.isNaN(start.z))
                    {
                        return null;
                    }

                    if (fsX == feX && fsY == feY && fsZ == feZ)
                    {
                        return returnLastUncollidableBlock
                                ? result
                                : null;
                    }

                    boolean xEq = true;
                    boolean yEq = true;
                    boolean zEq = true;
                    double x = 999.0;
                    double y = 999.0;
                    double z = 999.0;

                    if (feX > fsX)
                    {
                        x = fsX + 1.0;
                    }
                    else if (feX < fsX)
                    {
                        x = fsX + 0.0;
                    }
                    else
                    {
                        xEq = false;
                    }

                    if (feY > fsY)
                    {
                        y = fsY + 1.0;
                    }
                    else if (feY < fsY)
                    {
                        y = fsY + 0.0;
                    }
                    else
                    {
                        yEq = false;
                    }

                    if (feZ > fsZ)
                    {
                        z = fsZ + 1.0;
                    }
                    else if (feZ < fsZ)
                    {
                        z = fsZ + 0.0;
                    }
                    else
                    {
                        zEq = false;
                    }

                    double xOff = 999.0;
                    double yOff = 999.0;
                    double zOff = 999.0;
                    double diffX = end.x - start.x;
                    double diffY = end.y - start.y;
                    double diffZ = end.z - start.z;

                    if (xEq)
                    {
                        xOff = (x - start.x) / diffX;
                    }

                    if (yEq)
                    {
                        yOff = (y - start.y) / diffY;
                    }

                    if (zEq)
                    {
                        zOff = (z - start.z) / diffZ;
                    }

                    if (xOff == -0.0)
                    {
                        xOff = -1.0E-4D;
                    }

                    if (yOff == -0.0)
                    {
                        yOff = -1.0E-4D;
                    }

                    if (zOff == -0.0)
                    {
                        zOff = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (xOff < yOff && xOff < zOff)
                    {
                        enumfacing = feX > fsX
                                ? EnumFacing.WEST
                                : EnumFacing.EAST;

                        start = new Vec3d(x,
                                start.y + diffY * xOff,
                                start.z + diffZ * xOff);
                    }
                    else if (yOff < zOff)
                    {
                        enumfacing = feY > fsY
                                ? EnumFacing.DOWN
                                : EnumFacing.UP;

                        start = new Vec3d(start.x + diffX * yOff,
                                y,
                                start.z + diffZ * yOff);
                    }
                    else
                    {
                        enumfacing = feZ > fsZ
                                ? EnumFacing.NORTH
                                : EnumFacing.SOUTH;

                        start = new Vec3d(start.x + diffX * zOff,
                                start.y + diffY * zOff,
                                z);
                    }

                    fsX = MathHelper.floor(start.x)
                            - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    fsY = MathHelper.floor(start.y)
                            - (enumfacing == EnumFacing.UP ? 1 : 0);
                    fsZ = MathHelper.floor(start.z)
                            - (enumfacing == EnumFacing.SOUTH ? 1 : 0);

                    pos = new BlockPos(fsX, fsY, fsZ);
                    IBlockState state1 = access.getBlockState(pos);
                    Block block1 = state1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox
                            || state1.getMaterial() == Material.PORTAL
                            || state1.getCollisionBoundingBox(access, pos)
                                != Block.NULL_AABB)
                    {
                        if ((block1.canCollideCheck(state1, stopOnLiquid)
                                || collideCheck != null
                                && collideCheck.test(block1, pos, enumfacing))
                            && (blockChecker == null
                                || blockChecker.test(block1, pos, enumfacing)))
                        {
                            RayTraceResult raytraceresult1 =
                                crt.collisionRayTrace(
                                        state1, world, pos, start, end);

                            if (raytraceresult1 != null)
                            {
                                return raytraceresult1;
                            }
                        }
                        else
                        {
                            result = new RayTraceResult(
                                        RayTraceResult.Type.MISS,
                                        start,
                                        enumfacing,
                                        pos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? result : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

}
