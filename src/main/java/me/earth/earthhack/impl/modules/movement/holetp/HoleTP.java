package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleTP extends Module
{
    public static final double[] OFFSETS = new double[]{0.42D, 0.75D};

    public final Setting<Boolean> wide =
            register(new BooleanSetting("2x1s", true));
    public final Setting<Boolean> big  =
            register(new BooleanSetting("2x2s", false));
    protected boolean jumped;
    protected int packets;

    public HoleTP()
    {
        super("HoleTP", Category.Movement);
        this.listeners.add(new ListenerMotion(this));
    }

    public boolean isInHole()
    {
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        IBlockState blockState = mc.world.getBlockState(blockPos);
        return isBlockValid(blockState, blockPos);
    }

    protected boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
        if (blockState.getBlock() != Blocks.AIR) {
            return false;
        } else if (mc.player.getDistanceSq(blockPos) < 1.0D) {
            return false;
        } else if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        } else if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) {
            return false;
        } else {
            return HoleUtil.isHole(blockPos,true)[0] || (HoleUtil.is2x1(blockPos) && wide.getValue()) || (HoleUtil.is2x2Partial(blockPos) && big.getValue());
        }
    }

    protected double getNearestBlockBelow()
    {
        for (double y = mc.player.posY; y > 0; y -= 0.001) {
            if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0)) != null) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) {
                    return -1;
                }
                return y;
            }
        }
        return -1;
    }
}
