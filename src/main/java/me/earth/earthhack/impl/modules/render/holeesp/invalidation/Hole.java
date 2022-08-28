package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

public interface Hole {
    int getX();

    int getY();

    int getZ();

    int getMaxX();

    int getMaxZ();

    boolean isSafe();

    boolean is2x1();

    boolean is2x2();

    void invalidate();

    boolean isValid();

    default boolean isAirPart(BlockPos pos) {
        return isAirPart(pos.getX(), pos.getY(), pos.getZ());
    }

    default boolean isAirPart(int x, int y, int z) {
        return x >= getX()
            && y >= getY()
            && z >= getZ()
            && x < getMaxX()
            && y < getY() + 2
            && z < getMaxZ();
    }

    default double getDistanceSq(double x, double y, double z) {
        double xDiff = getX() - x;
        double yDiff = getY() - y;
        double zDiff = getZ() - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    default boolean contains(double x, double y, double z) {
        return x > this.getX() && x < this.getMaxX()
            && y >= this.getY() && y < this.getY() + 1
            && z > this.getZ() && z < this.getMaxZ();
    }

}
