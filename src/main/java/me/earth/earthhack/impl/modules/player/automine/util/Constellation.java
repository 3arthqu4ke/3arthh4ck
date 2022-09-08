package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

// TODO: more generic approach maybe map BlockPositions to BlockStates
public class Constellation implements IConstellation
{
    protected final EntityPlayer player;
    protected final BlockPos playerPos;
    protected final IBlockState state;
    protected final IBlockState playerState;
    protected final BlockPos pos;
    protected final AutoMine autoMine;
    protected boolean burrow;

    public Constellation(IBlockAccess world,
                         EntityPlayer player,
                         BlockPos pos,
                         BlockPos playerPos,
                         IBlockState state,
                         AutoMine autoMine)
    {
        this.player      = player;
        this.pos         = pos;
        this.playerPos   = playerPos;
        this.autoMine = autoMine;
        this.playerState = world.getBlockState(playerPos);
        this.state       = state;
    }

    @Override
    public boolean isAffected(BlockPos pos, IBlockState state)
    {
        return this.pos.equals(pos) && !this.state.equals(state);
    }

    @Override
    public boolean isValid(IBlockAccess world, boolean checkPlayerState)
    {
        // Can't test this with a FakePlayer!
        return (PositionUtil.getPosition(player).equals(playerPos)
                || isBurrow()
                    && autoMine.extraBurrowCheck.getValue()
                    && player.getDistanceSq(pos) < 1)
                && world.getBlockState(pos).equals(state)
                && (!checkPlayerState
                    || world.getBlockState(playerPos).equals(playerState));
    }

    public boolean isBurrow()
    {
        return burrow;
    }

    public void setBurrow(boolean burrow)
    {
        this.burrow = burrow;
    }

}
