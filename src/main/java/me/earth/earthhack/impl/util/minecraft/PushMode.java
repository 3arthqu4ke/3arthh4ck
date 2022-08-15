package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public enum PushMode implements Globals {
    None() {
        @Override
        public void pushOutOfBlocks(
            Entity entity, double x, double y, double z) {
            // NOP
        }
    },
    MP() {
        private final Random rand = new Random();

        @Override
        public void pushOutOfBlocks(
            Entity entity, double x, double y, double z) {
            BlockPos blockpos = new BlockPos(x, y, z);
            double d0 = x - (double)blockpos.getX();
            double d1 = y - (double)blockpos.getY();
            double d2 = z - (double)blockpos.getZ();
            if (mc.world.collidesWithAnyBlock(entity.getEntityBoundingBox()))
            {
                EnumFacing enumfacing = EnumFacing.UP;
                double d3 = Double.MAX_VALUE;

                if (!mc.world.isBlockFullCube(blockpos.west()) && d0 < d3)
                {
                    d3 = d0;
                    enumfacing = EnumFacing.WEST;
                }

                if (!mc.world.isBlockFullCube(blockpos.east()) && 1.0D - d0 < d3)
                {
                    d3 = 1.0D - d0;
                    enumfacing = EnumFacing.EAST;
                }

                if (!mc.world.isBlockFullCube(blockpos.north()) && d2 < d3)
                {
                    d3 = d2;
                    enumfacing = EnumFacing.NORTH;
                }

                if (!mc.world.isBlockFullCube(blockpos.south()) && 1.0D - d2 < d3)
                {
                    d3 = 1.0D - d2;
                    enumfacing = EnumFacing.SOUTH;
                }

                if (!mc.world.isBlockFullCube(blockpos.up()) && 1.0D - d1 < d3)
                {
                    d3 = 1.0D - d1;
                    enumfacing = EnumFacing.UP;
                }

                float f = rand.nextFloat() * 0.2F + 0.1F;
                float f1 = (float)enumfacing.getAxisDirection().getOffset();

                if (enumfacing.getAxis() == EnumFacing.Axis.X)
                {
                    entity.motionX = f1 * f;
                    entity.motionY *= 0.75D;
                    entity.motionZ *= 0.75D;
                }
                else if (enumfacing.getAxis() == EnumFacing.Axis.Y)
                {
                    entity.motionX *= 0.75D;
                    entity.motionY = f1 * f;
                    entity.motionZ *= 0.75D;
                }
                else if (enumfacing.getAxis() == EnumFacing.Axis.Z)
                {
                    entity.motionX *= 0.75D;
                    entity.motionY *= 0.75D;
                    entity.motionZ = f1 * f;
                }
            }
        }
    };

    public abstract void pushOutOfBlocks(
        Entity entity, double x, double y, double z);

    private static boolean isHeadspaceFree(BlockPos pos, int height)
    {
        for (int y = 0; y < height; y++)
        {
            if (!isOpenBlockSpace(pos.add(0, y, 0))) return false;
        }

        return true;
    }

    private static boolean isOpenBlockSpace(BlockPos pos)
    {
        IBlockState iblockstate = mc.world.getBlockState(pos);
        return !iblockstate.getBlock().isNormalCube(iblockstate, mc.world, pos);
    }

}
