package me.earth.earthhack.impl.util.ncp;

import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHistoryHelper;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Visible {
    public static final Visible INSTANCE = new Visible();

    private class RayChecker extends PositionHistoryChecker {
        protected boolean check(double x, double y, double z, float yaw, float pitch, int blockX, int blockY, int blockZ) {
            Vec3d direction = RotationUtil.getVec3d(yaw, pitch);
            return !checkRayTracing(x, y, z, direction.x, direction.y, direction.z, blockX, blockY, blockZ);
        }

        public boolean checkFlyingQueue(double x, double y, double z, float oldYaw, float oldPitch, int blockX, int blockY, int blockZ, PositionHistoryHelper history) {
            return super.checkFlyingQueue(x, y, z, oldYaw, oldPitch, blockX, blockY, blockZ, history);
        }
    }

    private final NcpInteractTrace rayTracing = new NcpInteractTrace();
    private final RayChecker checker = new RayChecker();

    public Visible() {
        rayTracing.setMaxSteps(60);
    }

    public boolean check(BlockPos pos) {
        return check(pos, 10);
    }

    public boolean check(BlockPos pos, int ticks) {
        checker.ticksToCheck = ticks;
        Entity entity = RotationUtil.getRotationPlayer();
        return check(entity.posX, entity.posY, entity.posZ,
                     entity.rotationYaw, entity.rotationPitch, pos);
    }

    public boolean check(double x, double y, double z,
                         float yaw, float pitch, BlockPos pos) {
        boolean collides;
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();
        double eyeY = y + RotationUtil.getRotationPlayer().getEyeHeight();
        if (isSameBlock(blockX, blockY, blockZ, x, eyeY, z)) {
            collides = false;
        } else {
            collides = !checker.checkFlyingQueue(x, eyeY, z, yaw, pitch, blockX, blockY, blockZ, AutoCrystal.POSITION_HISTORY);
        }

        return collides;
    }

    public static boolean isSameBlock(int x1, int y1, int z1,
                                      double x2, double y2, double z2) {
        return x1 == floor(x2)
            && z1 == floor(z2)
            && y1 == floor(y2);
    }

    private boolean checkRayTracing(double eyeX, double eyeY, double eyeZ,
                                    double dirX, double dirY, double dirZ,
                                    int blockX, int blockY, int blockZ) {
        int eyeBlockX = floor(eyeX);
        int eyeBlockY = floor(eyeY);
        int eyeBlockZ = floor(eyeZ);
        int bdX = blockX - eyeBlockX;
        int bdY = blockY - eyeBlockY;
        int bdZ = blockZ - eyeBlockZ;

        double tMinX = getMinTime(eyeX, eyeBlockX, dirX, bdX);
        double tMinY = getMinTime(eyeY, eyeBlockY, dirY, bdY);
        double tMinZ = getMinTime(eyeZ, eyeBlockZ, dirZ, bdZ);
        double tMaxX = getMaxTime(eyeX, eyeBlockX, dirX, tMinX);
        double tMaxY = getMaxTime(eyeY, eyeBlockY, dirY, tMinY);
        double tMaxZ = getMaxTime(eyeZ, eyeBlockZ, dirZ, tMinZ);

        double tCollide = Math.max(0.0, Math.max(tMinX, Math.max(tMinY, tMinZ)));
        double collideX = toBlock(eyeX + dirX * tCollide, blockX);
        double collideY = toBlock(eyeY + dirY * tCollide, blockY);
        double collideZ = toBlock(eyeZ + dirZ * tCollide, blockZ);

        if (tMinX > tMaxY && tMinX > tMaxZ ||
            tMinY > tMaxX && tMinY > tMaxZ ||
            tMinZ > tMaxX && tMaxZ > tMaxY) {
            collideX = postCorrect(blockX, bdX, collideX);
            collideY = postCorrect(blockY, bdY, collideY);
            collideZ = postCorrect(blockZ, bdZ, collideZ);
        }

        if (tMinX == tCollide) {
            collideX = Math.round(collideX);
        }

        if (tMinY == tCollide) {
            collideY = Math.round(collideY);
        }

        if (tMinZ == tCollide) {
            collideZ = Math.round(collideZ);
        }

        rayTracing.set(eyeX, eyeY, eyeZ, collideX, collideY, collideZ, blockX, blockY, blockZ);
        rayTracing.loop();

        boolean collides;
        if (rayTracing.collides) {
            collides = true;
        } else {
            collides = rayTracing.getStepsDone() > rayTracing.getMaxSteps();
        }

        return collides;
    }

    private double postCorrect(int blockC, int bdC, double collideC) {
        int ref = bdC < 0 ? blockC + 1 : blockC;
        if (floor(collideC) == ref) {
            return collideC;
        } else {
            return ref;
        }
    }

    private double getMinTime(double eye, int eyeBlock, double dir, int blockDiff) {
        if (blockDiff == 0) {
            return 0.0;
        }

        double eyeOffset = Math.abs(eye - eyeBlock);
        return ((dir < 0.0 ? eyeOffset : 1.0 - eyeOffset)
            + (double) (Math.abs(blockDiff) - 1)) / Math.abs(dir);
    }

    private double getMaxTime(double eye, int eyeBlock, double dir, double tMin) {
        if (dir == 0.0) {
            return Double.MAX_VALUE;
        }

        if (tMin == 0.0) {
            double eyeOffset = Math.abs(eye - eyeBlock);
            return (dir < 0.0 ? eyeOffset : 1.0 - eyeOffset) / Math.abs(dir);
        }

        return tMin + 1.0 /  Math.abs(dir);
    }

    private double toBlock(double coord, int block) {
        int blockDiff = block - floor(coord);
        if (blockDiff == 0) {
            return coord;
        }
        else {
            return Math.round(coord);
        }
    }

    public static int floor(double num) {
        int floor = (int) num;
        return floor == num
            ? floor
            : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

}
