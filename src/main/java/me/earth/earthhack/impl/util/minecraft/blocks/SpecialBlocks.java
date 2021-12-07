package me.earth.earthhack.impl.util.minecraft.blocks;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.ActionManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Utility for blocks that have special behaviour
 * for example that need to be crouched on when clicked.
 */
public class SpecialBlocks implements Globals
{
    /**
     * Blocks that open guis when clicked.
     */
    public static final Set<Block> BAD_BLOCKS = Sets.newHashSet(
            Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND,
            Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
            Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    /**
     * A Set of all Shulkers.
     */
    public static final Set<Block> SHULKERS = Sets.newHashSet(
            Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    /**
     * A Set of all PressurePlates.
     */
    public static final Set<Block> PRESSURE_PLATES = Sets.newHashSet(
            Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Blocks.STONE_PRESSURE_PLATE,
            Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Blocks.WOODEN_PRESSURE_PLATE
    );
    /**
     * A Predicate that tests if the packet is instanceof {@link
     * CPacketPlayerTryUseItemOnBlock} and then returns {@link
     * SpecialBlocks#shouldSneak(Block, boolean)}, for the block at the position
     * the packet is targeting and <tt>false</tt>.
     */
    public static final Predicate<Packet<?>> PACKETCHECK = p ->
            p instanceof CPacketPlayerTryUseItemOnBlock
                    && shouldSneak(((CPacketPlayerTryUseItemOnBlock) p).getPos(),
                    false);
    /**
     * Same as {@link SpecialBlocks#PACKETCHECK} but also accepts an
     * {@link IBlockAccess} for
     * {@link SpecialBlocks#shouldSneak(BlockPos, IBlockAccess, boolean)}.
     */
    public static final BiPredicate<Packet<?>, IBlockAccess> ACCESS_CHECK =
            (p, b) -> p instanceof CPacketPlayerTryUseItemOnBlock
                    && shouldSneak(((CPacketPlayerTryUseItemOnBlock) p)
                            .getPos(),
                    b,
                    false);

    /**
     * Calls {@link SpecialBlocks#shouldSneak(BlockPos, IBlockAccess, boolean)}
     * for the given parameters and <tt>mc.world</tt>,
     */
    public static boolean shouldSneak(BlockPos pos, boolean manager)
    {
        return shouldSneak(pos, mc.world, manager);
    }

    /**
     * Calls {@link SpecialBlocks#shouldSneak(Block, boolean)} for the Block
     * provided by the IBlockState provided by the provider at the given
     * position.
     */
    public static boolean shouldSneak(BlockPos pos,
                                      IBlockAccess provider,
                                      boolean manager)
    {
        return shouldSneak(provider.getBlockState(pos).getBlock(), manager);
    }

    /**
     * Determines if we need to crouch when we rightClick the given block.
     *
     * @param block   the block we are clicking click.
     * @param manager if {@link ActionManager} should be taken into account.
     * @return <tt>true</tt> if we have to sneak.
     */
    public static boolean shouldSneak(Block block, boolean manager)
    {
        if (manager && Managers.ACTION.isSneaking())
        {
            return false;
        }

        return BAD_BLOCKS.contains(block) || SHULKERS.contains(block);
    }
}
