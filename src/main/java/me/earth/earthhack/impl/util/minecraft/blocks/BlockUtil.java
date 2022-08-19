package me.earth.earthhack.impl.util.minecraft.blocks;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.util.EnumFacing.HORIZONTALS;

public class BlockUtil implements Globals
{
    /**
     * Checks if the given Position (which should be
     * the BlockPosition underneath the crystal) lies
     * within the given ranges from the mc.player.
     *
     * @param pos the position to check.
     * @param placeRange the placeRange.
     * @param placeTrace the placeTrace.
     * @param combinedTrace the combinedTrace.
     * @return <tt>true</tt> if in range.
     */
    public static boolean isCrystalPosInRange(BlockPos pos,
                                              double placeRange,
                                              double placeTrace,
                                              double combinedTrace)
    {
        double distance = getDistanceSq(pos);
        if (distance > MathUtil.square(placeRange))
        {
            return false;
        }

        if (distance > MathUtil.square(placeTrace)
                && !RayTraceUtil.raytracePlaceCheck(mc.player, pos))
        {
            return false;
        }

        if (distance <= MathUtil.square(combinedTrace))
        {
            return true;
        }

        return RayTraceUtil.canBeSeen(
                new Vec3d(pos.getX() + 0.5,
                          pos.getY() + 2.7,
                          pos.getZ() + 0.5),
                mc.player);
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2)
    {
        return canPlaceCrystal(pos, ignoreCrystals, noBoost2, null);
    }

    /**
     * @return {@link BlockUtil#getDistanceSq(Entity, BlockPos)} for the pos
     *          and {@link RotationUtil#getRotationPlayer()}
     */
    public static double getDistanceSq(BlockPos pos)
    {
        return getDistanceSq(RotationUtil.getRotationPlayer(), pos);
    }

    /**
     * @param from the entity to get the distanceSq from
     * @param to the position
     * @return {@link Entity#getDistanceSqToCenter(BlockPos)}.
     */
    public static double getDistanceSq(Entity from, BlockPos to)
    {
        return from.getDistanceSqToCenter(to);
    }

    public static double getDistanceSqDigging(BlockPos to)
    {
        return getDistanceSqDigging(RotationUtil.getRotationPlayer(), to);
    }

    /**
     * Calls {@link BlockUtil#sphere(BlockPos, double, Predicate)}
     * for {@link PositionUtil#getPosition()}, the given radius and the
     * predicate.
     *
     * @param radius the radius of the sphere.
     * @param predicate the predicate to apply.
     * @return {@link BlockUtil#sphere(BlockPos, double, Predicate)}.
     */
    public static boolean sphere(double radius,
                                 Predicate<BlockPos> predicate)
    {
        return sphere(PositionUtil.getPosition(), radius, predicate);
    }

    /**
     * Generates a sphere of the BlockPositions that lie
     * around the given pos within the given radius and
     * tests the given predicate on all of them.
     * If the predicate returns <tt>true</tt> the method returns
     * <tt>false</tt> immediately.
     *
     * @param pos the center of the sphere.
     * @param r the radius of the sphere.
     * @param predicate is applied to all positions.
     * @return <tt>true</tt> if all predicates returned <tt>false</tt>.
     */
    public static boolean sphere(BlockPos pos,
                                 double r,
                                 Predicate<BlockPos> predicate)
    {
        int tested = 0;
        double rSquare = r * r;
        for (int x = pos.getX() - (int) r; x <= pos.getX() + r; x++)
        {
            for (int z = pos.getZ() - (int) r; z <= pos.getZ() + r; z++)
            {
                for (int y = pos.getY() - (int) r; y < pos.getY() + r; y++)
                {
                    double dist = (pos.getX() - x) * (pos.getX() - x)
                                + (pos.getZ() - z) * (pos.getZ() - z)
                                + (pos.getY() - y) * (pos.getY() - y);

                    if (dist < rSquare && (tested++) > 0 && predicate.test(new BlockPos(x, y, z)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * @param from the entity to get the distanceSq from
     * @param to the position
     * @return distanceSq as used by
     *         {@link NetHandlerPlayServer#processPlayerDigging}.
     */
    public static double getDistanceSqDigging(Entity from, BlockPos to)
    {
        double x = from.posX - (to.getX() + 0.5);
        double y = from.posY - (to.getY() + 0.5) + 1.5;
        double z = from.posZ - (to.getZ() + 0.5);
        return x * x + y * y + z * z;
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2,
                                          List<Entity> entities)
    {
        return canPlaceCrystal(pos, ignoreCrystals, noBoost2, entities, noBoost2, 0);
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2,
                                          List<Entity> entities,
                                          boolean ignoreBoost2Entities,
                                          long deathTime)
    {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK)
        {
            return false;
        }

        return checkBoost(pos, ignoreCrystals, noBoost2, entities,
                          ignoreBoost2Entities, deathTime);
    }

    public static boolean canPlaceCrystalReplaceable(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2,
                                          List<Entity> entities,
                                          boolean ignoreBoost2Entities,
                                          long deathTime)
    {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN
            && state.getBlock() != Blocks.BEDROCK
            && !state.getMaterial().isReplaceable())
        {
            return false;
        }

        return checkBoost(pos, ignoreCrystals, noBoost2, entities,
                ignoreBoost2Entities, deathTime);
    }

    public static boolean checkBoost(BlockPos pos,
                                     boolean ignoreCrystals,
                                     boolean noBoost2,
                                     List<Entity> entities,
                                     boolean ignoreBoost2Entities,
                                     long deathTime)
    {
        BlockPos boost  = pos.up();
        if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR
            || !checkEntityList(boost, ignoreCrystals, entities, deathTime))
        {
            return false;
        }

        if (!noBoost2)
        {
            BlockPos boost2 = boost.up();

            if (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
            {
                return false;
            }

            return ignoreBoost2Entities
                || checkEntityList(boost2, ignoreCrystals, entities, deathTime);
        }

        return true;
    }

    public static boolean isSemiSafe(EntityPlayer player,
                                     boolean ignoreCrystals,
                                     boolean noBoost2)
    {
        BlockPos origin = PositionUtil.getPosition(player);
        int i = 0;
        for (EnumFacing face : HORIZONTALS)
        {
            BlockPos off = origin.offset(face);
            if (mc.world.getBlockState(off).getBlock() != Blocks.AIR) i++;
        }
        return i >= 3;
    }

    public static boolean canBeFeetPlaced(EntityPlayer player,
                                          boolean ignoreCrystals,
                                          boolean noBoost2)
    {
        BlockPos origin = PositionUtil.getPosition(player).down();
        for (EnumFacing face : HORIZONTALS)
        {
            BlockPos off = origin.offset(face);
            IBlockState state = mc.world.getBlockState(off);
            if (canPlaceCrystal(off, ignoreCrystals, noBoost2)) return true;
            BlockPos off2 = off.offset(face);
            if (canPlaceCrystal(off2, ignoreCrystals, noBoost2)
                    && state.getBlock() == Blocks.AIR) return true;
        }
        return false;
    }

    public static boolean canPlaceCrystalFuture(BlockPos pos,
                                                boolean ignoreCrystals,
                                                boolean noBoost2)
    {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK)
        {
            return false;
        }

        BlockPos boost  = pos.up();
        if (!checkEntityList(boost, ignoreCrystals, null))
        {
            return false;
        }

        if (mc.world.getBlockState(boost).getBlock() == Blocks.BEDROCK)
        {
            return false;
        }

        if (!noBoost2)
        {
            BlockPos boost2 = boost.up();

            if (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
            {
                return false;
            }

            return checkEntityList(boost2, ignoreCrystals, null);
        }

        return true;
    }

    public static boolean isAtFeet(List<EntityPlayer> players,
                                   BlockPos pos,
                                   boolean ignoreCrystals,
                                   boolean noBoost2)
    {
        for (EntityPlayer player : players)
        {
            if (Managers.FRIENDS.contains(player)
                    || player == mc.player) continue;
            if (isAtFeet(player, pos, ignoreCrystals, noBoost2)) return true;
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if a crystal at the given
     * position is perpendicular to the given player
     *
     * @param player the player to check
     * @param pos the position to check
     * @return <tt>true</tt> if a crystal placed
     * on the given position would block blocks being
     * placed at the given player's feet
     */
    public static boolean isAtFeet(EntityPlayer player,
                                   BlockPos pos,
                                   boolean ignoreCrystals,
                                   boolean noBoost2)
    {
        BlockPos up = pos.up();
        if (!canPlaceCrystal(pos, ignoreCrystals, noBoost2)) return false;
        for (EnumFacing face : HORIZONTALS)
        {
            BlockPos off = up.offset(face);
            //IBlockState state = mc.world.getBlockState(off);
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(off))
                    .contains(player))
            {
                return true;
            }

            BlockPos off2 = off.offset(face);
            //IBlockState offState = mc.world.getBlockState(off2);
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(off2))
                    .contains(player))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if a position exists
     * such that you can place a bed whose headpiece is
     * on the given position.
     *
     * @param pos the given position.
     * @param newerVer taking 1.13+ mechanic into account.
     * @return <tt>true</tt> if bed can be placed.
     */
    public static boolean canPlaceBed(BlockPos pos, boolean newerVer)
    {
        if (!bedBlockCheck(pos, newerVer))
        {
            return false;
        }

        for (EnumFacing facing : HORIZONTALS)
        {
            BlockPos horizontal = pos.offset(facing);
            if (bedBlockCheck(horizontal, newerVer)
                    && getFacing(horizontal) != null)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean checkEntityList(BlockPos pos,
                                          boolean ignoreCrystals,
                                          List<Entity> entities)
    {
        return checkEntityList(pos, ignoreCrystals, entities, 0);
    }

    public static boolean checkEntityList(BlockPos pos,
                                          boolean ignoreCrystals,
                                          List<Entity> entities,
                                          long deathTime)
    {
        if (entities == null)
        {
            return checkEntities(pos, ignoreCrystals, deathTime);
        }

        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities)
        {
            if (checkEntity(entity, ignoreCrystals, deathTime)
                    && entity.getEntityBoundingBox().intersects(bb))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isAir(BlockPos pos)
    {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    public static boolean checkEntities(BlockPos pos,
                                        boolean ignoreCrystals,
                                        long deathTime)
    {
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)))
        {
            if (checkEntity(entity, ignoreCrystals, deathTime))
            {
                return false;
            }
        }

        return true;
    }

    private static boolean checkEntity(Entity entity,
                                       boolean ignoreCrystals,
                                       long deathTime)
    {
        if (entity == null)
        {
            return false;
        }

        if (entity instanceof EntityEnderCrystal)
        {
            if (ignoreCrystals)
            {
                return false;
            }

            return !entity.isDead
                    || !Managers.SET_DEAD.passedDeathTime(entity, deathTime);
        }

        return !EntityUtil.isDead(entity);
    }

    /**
     * @param pos the position.
     * @return {@link BlockUtil#getFacing(BlockPos, IBlockAccess)}
     *          for the position and mc.world.
     */
    public static EnumFacing getFacing(BlockPos pos)
    {
        return getFacing(pos, mc.world);
    }

    /**
     * Checks all blocks around the given position until
     * it finds one where the Material of the BlockState
     * at that pos is not replaceable and returns the offset
     * between the given and that position.
     *
     * @param pos the position to get a facing for.
     * @param provider provides the BlockStates.
     * @return a facing for the given position.
     */
    public static EnumFacing getFacing(BlockPos pos, IBlockAccess provider)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (!provider.getBlockState(pos.offset(facing))
                         .getMaterial()
                         .isReplaceable())
            {
                return facing;
            }
        }

        return null;
    }

    /**
     * Gets the BlockState at the position and checks if
     * its Material is replaceable.
     *
     * @param pos the pos to check.
     * @return {@link Material#isReplaceable()} for the position.
     */
    public static boolean isReplaceable(BlockPos pos)
    {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    /**
     * If players just stand against a block
     * e.g. in feetTraps they can't block you
     * from placing (unless they phased in)
     * even tho their bounding box intersects
     * the positions bounding box. This method
     * finds out if a player is in such a position.
     *
     * @param pos the pos to check.
     * @param player the player that blocks.
     * @return <tt>true</tt> if he blocks the position.
     */
    public static boolean isBlocking(BlockPos pos,
                                     EntityPlayer player,
                                     BlockingType type)
    {
        AxisAlignedBB posBB = new AxisAlignedBB(pos);
        if (type == BlockingType.Strict || type == BlockingType.Crystals)
        {
            return player.getEntityBoundingBox()
                         .intersects(posBB);
        }

        if (type == BlockingType.PacketFly)
        {
            return player.getEntityBoundingBox()
                         .expand(-0.0625, -0.0625, -0.0625)
                         .intersects(posBB);
        }

        if (type == BlockingType.Full
                && player.getEntityBoundingBox()
                         .expand(-0.0625, -0.0625, -0.0625)
                         .intersects(posBB))
        {
            return true;
        }

        AxisAlignedBB bb = player.getEntityBoundingBox();
        if (type == BlockingType.All)
        {
            bb = bb.expand(-0.0625, -0.0625, -0.0625);
        }

        // TODO: new mode that checks non expanded then expanded?
        if (type == BlockingType.NoPacketFly && bb.intersects(posBB))
        {
            BlockPos playerPos = new BlockPos(player);
            // This doesn't give full burrow protection,
            // but should suffice in most cases.
            if (playerPos.getX() != pos.getX()
                    || playerPos.getZ() != pos.getZ())
            {
                // player trying to block with his upper body
                if (playerPos.getY() < pos.getY())
                {
                    return mc.world.getBlockState(pos.down())
                                   .getMaterial()
                                   .isReplaceable();
                }
                else // normal feettrap
                {
                    return mc.world.getBlockState(pos.up())
                                   .getMaterial()
                                   .isReplaceable();
                }
            }

            return true;
        }

        return false;
    }

    private static boolean bedBlockCheck(BlockPos pos, boolean newerVer)
    {
        return mc.world.getBlockState(pos)
                       .getMaterial()
                       .isReplaceable()
                && (newerVer
                        || !mc.world.getBlockState(pos.down())
                                    .getMaterial()
                                    .isReplaceable());
    }

}
