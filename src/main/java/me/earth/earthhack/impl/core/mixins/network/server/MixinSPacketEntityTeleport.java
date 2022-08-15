package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.impl.core.ducks.network.ISPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SPacketEntityTeleport.class)
public abstract class MixinSPacketEntityTeleport implements ISPacketEntityTeleport {
    @Unique
    private boolean setByPackets;

    @Override
    public boolean hasBeenSetByPackets() {
        return setByPackets;
    }

    @Override
    public void setSetByPackets(boolean setByPackets) {
        this.setByPackets = setByPackets;
    }

}
