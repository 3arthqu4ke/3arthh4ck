package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.impl.core.ducks.network.ISPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SPacketSpawnObject.class)
public abstract class MixinSPacketSpawnObject implements ISPacketSpawnObject {
    @Unique
    private boolean attacked;

    @Override
    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    @Override
    public boolean isAttacked() {
        return attacked;
    }

}
