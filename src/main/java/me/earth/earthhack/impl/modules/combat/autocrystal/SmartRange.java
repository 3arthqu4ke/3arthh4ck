package me.earth.earthhack.impl.modules.combat.autocrystal;

import net.minecraft.util.math.BlockPos;

public enum SmartRange {
    None() {
        @Override
        public boolean isOutsideBreakRange(BlockPos pos, AutoCrystal module) {
            return false;
        }

        @Override
        public boolean isOutsideBreakRange(double x, double y, double z,
                                           AutoCrystal module) {
            return false;
        }
    },
    Normal() {
        @Override
        public boolean isOutsideBreakRange(double x, double y, double z,
                                           AutoCrystal module) {
            return !module.rangeHelper.isCrystalInRange(x, y, z, 0);
        }
    },
    Extrapolated() {
        @Override
        public boolean isOutsideBreakRange(double x, double y, double z,
                                           AutoCrystal module) {
            return !module.rangeHelper.isCrystalInRange(x, y, z,
                                                        module.smartTicks
                                                            .getValue());
        }
    },
    All() {
        @Override
        public boolean isOutsideBreakRange(double x, double y, double z,
                                           AutoCrystal module) {
            return Normal.isOutsideBreakRange(x, y, z, module)
                && Extrapolated.isOutsideBreakRange(x, y, z, module);
        }
    };

    /**
     * @return <tt>true</tt> if outside range.
     */
    public boolean isOutsideBreakRange(BlockPos pos, AutoCrystal module) {
        return isOutsideBreakRange(pos.getX() + 0.5f, pos.getY() + 1,
                                   pos.getZ() + 0.5f, module );
    }

    public abstract boolean isOutsideBreakRange(double x, double y, double z,
                                                AutoCrystal module);

}
