package me.earth.earthhack.impl.core.mixins.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EnumConnectionState.class)
public interface IEnumConnectionState
{
    @Accessor(value = "STATES_BY_CLASS")
    Map<Class<? extends Packet<?>> , EnumConnectionState> getStatesByClass();

}
