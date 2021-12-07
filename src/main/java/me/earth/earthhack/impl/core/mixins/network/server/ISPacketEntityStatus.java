package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.play.server.SPacketEntityStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityStatus.class)
public interface ISPacketEntityStatus
{
    @Accessor("entityId")
    int getEntityId();

    @Accessor("logicOpcode")
    byte getLogicOpcode();

}
