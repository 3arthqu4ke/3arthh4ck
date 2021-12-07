package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntityHeadLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityHeadLook.class)
public interface ISPacketEntityHeadLook
{
    @Accessor(value = "entityId")
    int getEntityId();
}
