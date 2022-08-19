package me.earth.earthhack.impl.util.ncp;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class NcpInteractTrace extends NcpTrace implements Globals {
    protected final boolean strict = false;
    protected int lastBx;
    protected int lastBy;
    protected int lastBz;
    protected int targetX;
    protected int targetY;
    protected int targetZ;

    public NcpInteractTrace() {
        this.forceStepEndPos = false;
    }

    public void set(double x0, double y0, double z0, double x1, double y1, double z1) {
        set(x0, y0, z0, x1, y1, z1, Visible.floor(x1), Visible.floor(y1), Visible.floor(z1));
    }

    public void set(double x0, double y0, double z0, double x1, double y1, double z1, int targetX, int targetY, int targetZ) {
        super.set(x0, y0, z0, x1, y1, z1);
        collides = false;
        lastBx = blockX;
        lastBy = blockY;
        lastBz = blockZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    private boolean doesCollide(int blockX, int blockY, int blockZ) {
        BlockPos pos = new BlockPos(blockX, blockY, blockZ);
        IBlockState state = mc.world.getBlockState(pos);
        // BlockProperties.F_LIQUID | BlockProperties.F_IGN_PASSABLE | BlockProperties.F_STAIRS | BlockProperties.F_VARIABLE
        if (!state.getMaterial().isSolid()
            || state.getMaterial().isLiquid()
            || !state.getBlock().canCollideCheck(state, false) // <- TODO: check that one
        ) {

            return false;
        }
        // !blockAccess.isFullBounds(blockX, blockY, blockZ) <- TODO?????
        return state.getBoundingBox(mc.world, pos).getAverageEdgeLength() == 1.0;
    }

    public boolean isTargetBlock() {
        return targetX != Integer.MAX_VALUE && blockX == targetX && blockY == targetY && blockZ == targetZ;
    }

    @Override
    protected boolean step(int blockX, int blockY, int blockZ, double oX, double oY, double oZ, double dT, boolean isPrimary) {
        if (isTargetBlock() || !doesCollide(blockX, blockY, blockZ)) {
            if (isPrimary) {
                lastBx = blockX;
                lastBy = blockY;
                lastBz = blockZ;
            }

            return true;
        }

        collides = true;
        return false;
    }

}
