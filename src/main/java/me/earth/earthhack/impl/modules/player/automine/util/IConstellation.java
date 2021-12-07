package me.earth.earthhack.impl.modules.player.automine.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Represents a constellation of a player to a block that
 * should be mined by Automine.
 */
public interface IConstellation
{
    /**
     * Updates this constellation.
     * @param automine the AutoMine context.
     */
    default void update(IAutomine automine) { }

    /**
     * @param pos the pos where the blockchange happened.
     * @param state the new blockstate.
     * @return <tt>true</tt> if this is affected by the blockchange.
     */
    boolean isAffected(BlockPos pos, IBlockState state);

    /**
     * @param world the world to check.
     * @param checkPlayerState if the players state should be checked.
     * @return <tt>true</tt> if this Constellation is still valid.
     */
    boolean isValid(IBlockAccess world, boolean checkPlayerState);

    /**
     * @return <tt>true</tt> if this calculation shouldn't be improved.
     */
    default boolean cantBeImproved()
    {
        return true;
    }

}
