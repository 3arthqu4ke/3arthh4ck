package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

public class MutPos extends BlockPos.MutableBlockPos {
    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void incrementX(int by) {
        this.x += by;
    }

    public void incrementY(int by) {
        this.y += by;
    }

    public void incrementZ(int by) {
        this.z += by;
    }

}
