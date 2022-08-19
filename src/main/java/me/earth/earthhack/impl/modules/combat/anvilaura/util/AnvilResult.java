package me.earth.earthhack.impl.modules.combat.anvilaura.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnvilResult implements Globals, Comparable<AnvilResult>
{
    // TODO: Smart AnvilBB!
    private static final AxisAlignedBB ANVIL_BB =
            new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

    private final Set<BlockPos> positions;
    private final Set<BlockPos> mine;
    private final Set<BlockPos> trap;
    private final EntityPlayer player;
    private final BlockPos playerPos;
    private final BlockPos pressurePos;
    private final boolean validPressure;
    private final boolean fallingEntities;
    private final boolean specialPressure;

    public AnvilResult(Set<BlockPos> positions,
                       Set<BlockPos> mine,
                       Set<BlockPos> trap,
                       EntityPlayer player,
                       BlockPos playerPos,
                       BlockPos pressurePos,
                       boolean validPressure,
                       boolean fallingEntities,
                       boolean specialPressure)
    {
        this.positions = positions;
        this.mine = mine;
        this.trap = trap;
        this.player = player;
        this.playerPos = playerPos;
        this.pressurePos = pressurePos;
        this.validPressure = validPressure;
        this.fallingEntities = fallingEntities;
        this.specialPressure = specialPressure;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public BlockPos getPressurePos()
    {
        return pressurePos;
    }

    public BlockPos getPlayerPos()
    {
        return playerPos;
    }

    public Set<BlockPos> getPositions()
    {
        return positions;
    }

    public Set<BlockPos> getMine()
    {
        return mine;
    }

    public Set<BlockPos> getTrap()
    {
        return trap;
    }

    public boolean hasValidPressure()
    {
        return validPressure;
    }

    public boolean hasFallingEntities()
    {
        return fallingEntities;
    }

    public boolean hasSpecialPressure()
    {
        return specialPressure;
    }

    @Override
    public int hashCode()
    {
        return player.getEntityId() * 31 + playerPos.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof AnvilResult)
        {
            return ((AnvilResult) obj).player.equals(this.player)
                    && ((AnvilResult) obj).playerPos.equals(this.playerPos);
        }

        return false;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(AnvilResult o)
    {
        if (this.equals(o))
        {
            return 0;
        }

        int r = Double.compare(BlockUtil.getDistanceSq(o.playerPos),
                               BlockUtil.getDistanceSq(this.playerPos));
        return r == 0 ? 1 : r;
    }

    public static Set<AnvilResult> create(List<EntityPlayer> players,
                                          List<Entity> entities,
                                          double minY,
                                          double range)
    {
        Set<AnvilResult> results = new TreeSet<>();
        EntityPlayer rotation = RotationUtil.getRotationPlayer();
        for (EntityPlayer player : players)
        {
            if (player.posY < 0
                || EntityUtil.isDead(player)
                || player.equals(RotationUtil.getRotationPlayer())
                || player.equals(mc.player)
                || Managers.FRIENDS.contains(player))
            {
                continue;
            }

            double distance = MathUtil.square(player.posX - rotation.posX)
                            + MathUtil.square(player.posZ - rotation.posZ);

            if (distance > MathUtil.square(range))
            {
                continue;
            }

            for (BlockPos pos : PositionUtil.getBlockedPositions(
                    player.getEntityBoundingBox(), 1.0))
            {
                if (player.getEntityBoundingBox()
                          .intersects(ANVIL_BB.offset(pos)))
                {
                    checkPos(player, pos, results, entities, minY, range);
                }
            }
        }

        return results;
    }

    private static void checkPos(EntityPlayer player,
                                 BlockPos playerPos,
                                 Set<AnvilResult> results,
                                 List<Entity> entities,
                                 double minY,
                                 double range)
    {
        int x = playerPos.getX();
        int z = playerPos.getZ();

        BlockPos upUp = playerPos.up(2);
        Set<BlockPos> trap = new LinkedHashSet<>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos trapPos = upUp.offset(facing);
            if (ObbyModule.HELPER.getBlockState(trapPos)
                                 .getMaterial()
                                 .isReplaceable())
            {
                trap.add(trapPos);
            }
        }

        boolean validPressure = true;
        BlockPos pressure = playerPos;
        boolean specialPressure = false;
        Set<BlockPos> mine = new LinkedHashSet<>();
        IBlockState playerState = ObbyModule.HELPER.getBlockState(pressure);
        if (!playerState.getMaterial().isReplaceable()
                && !SpecialBlocks.PRESSURE_PLATES
                                 .contains(playerState.getBlock()))
        {
            if (playerState.getBlock() == Blocks.ANVIL)
            {
                validPressure = false;
                mine.add(pressure);
            }
            else if (!mc.world.mayPlace(
                        Blocks.ANVIL, pressure, true, EnumFacing.UP, null)
                && playerState.getBoundingBox(
                        ObbyModule.HELPER, pressure).maxY < 1.0)
            {
                // this means the Anvil will break when it falls on this block.
                specialPressure = true;
            }

            pressure = playerPos.up();
            playerState = ObbyModule.HELPER.getBlockState(pressure);
            if (!playerState.getMaterial().isReplaceable())
            {
                if (playerState.getBlock() == Blocks.ANVIL)
                {
                    mine.add(pressure);
                }
                else
                {
                    return;
                }
            }
        }

        if (validPressure && !specialPressure)
        {
            BlockPos pressureDown = pressure.down();
            IBlockState state = ObbyModule.HELPER.getBlockState(pressureDown);
            if (!isTopSolid(pressureDown, state.getBlock(), state,
                            EnumFacing.UP, ObbyModule.HELPER)
                    && !(state.getBlock() instanceof BlockFence))
            {
                validPressure = false;
            }
        }

        BlockPos lowest = null;
        boolean fallingEntities = false;
        double yPos = RotationUtil.getRotationPlayer().posY;
        Set<BlockPos> positions = new LinkedHashSet<>();
        for (double y = yPos - range; y < yPos + range; y++)
        {
            BlockPos pos = new BlockPos(x, y, z);
            fallingEntities = fallingEntities || checkForFalling(pos, entities);
            if (y < player.posY + minY)
            {
                continue;
            }


            if (!BlockFalling.canFallThrough(
                    ObbyModule.HELPER.getBlockState(pos)))
            {
                break;
            }

            if (lowest == null)
            {
                lowest = pos;
            }

            positions.add(pos);
        }

        if (lowest == null)
        {
            return;
        }

        boolean bad = false;
        for (int y = pressure.getY(); y < lowest.getY(); y++)
        {
            BlockPos pos = new BlockPos(x, y, z);
            fallingEntities = fallingEntities || checkForFalling(pos, entities);
            if (pos.getY() == pressure.getY())
            {
                continue;
            }

            IBlockState state = ObbyModule.HELPER.getBlockState(pos);
            if (!BlockFalling.canFallThrough(state))
            {
                if (state.getBlock() == Blocks.ANVIL)
                {
                    mine.add(pos);
                    continue;
                }

                bad = true;
                break;
            }
        }

        if (bad)
        {
            return;
        }

        results.add(new AnvilResult(positions, mine, trap, player, playerPos,
                                    pressure, validPressure, fallingEntities,
                                    specialPressure));
    }

    private static boolean checkForFalling(BlockPos pos, List<Entity> entities)
    {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities)
        {
            if (entity instanceof EntityFallingBlock
                    && !entity.isDead
                    && entity.getEntityBoundingBox().intersects(bb))
            {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({"deprecation", "SameParameterValue"})
    private static boolean isTopSolid(BlockPos pos, Block block,
                                      IBlockState base_state,
                                      EnumFacing side,
                                      IBlockAccess world)
    {
        if (base_state.isTopSolid() && side == EnumFacing.UP) // Short circuit to vanilla function if its true
            return true;

        if (block instanceof BlockSlab)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            return base_state.isFullBlock()
                || (state.getValue(BlockSlab.HALF)
                        == BlockSlab.EnumBlockHalf.TOP && side == EnumFacing.UP)
                || (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM
                        && side == EnumFacing.DOWN);
        }
        else if (block instanceof BlockFarmland)
        {
            return (side != EnumFacing.DOWN && side != EnumFacing.UP);
        }
        else if (block instanceof BlockStairs)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            boolean flipped = state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
            BlockStairs.EnumShape shape = state.getValue(BlockStairs.SHAPE);
            EnumFacing facing = state.getValue(BlockStairs.FACING);
            if (side == EnumFacing.UP) return flipped;
            if (side == EnumFacing.DOWN) return !flipped;
            if (facing == side) return true;
            if (flipped)
            {
                if (shape == BlockStairs.EnumShape.INNER_LEFT ) return side == facing.rotateYCCW();
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) return side == facing.rotateY();
            }
            else
            {
                if (shape == BlockStairs.EnumShape.INNER_LEFT ) return side == facing.rotateY();
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) return side == facing.rotateYCCW();
            }
            return false;
        }
        else if (block instanceof BlockSnow)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            return state.getValue(BlockSnow.LAYERS) >= 8;
        }
        else if (block instanceof BlockHopper && side == EnumFacing.UP)
        {
            return true;
        }
        else if (block instanceof BlockCompressedPowered)
        {
            return true;
        }

        return base_state.isTopSolid();
    }

}
