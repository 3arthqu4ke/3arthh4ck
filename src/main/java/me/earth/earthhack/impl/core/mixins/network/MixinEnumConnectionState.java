package me.earth.earthhack.impl.core.mixins.network;

import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnumConnectionState.class)
public abstract class MixinEnumConnectionState
{
    @Inject(
        method = "getFromPacket",
        at = @At("HEAD"),
        cancellable = true)
    private static void getFromPacketHook(Packet<?> packet,
                                CallbackInfoReturnable<EnumConnectionState> cir)
    {
        if (packet instanceof CustomPacket)
        {
            cir.setReturnValue(((CustomPacket) packet).getState());
        }
    }

    @Inject(
        method = "getPacketId",
        at = @At("HEAD"),
        cancellable = true)
    public void getPacketIdHook(EnumPacketDirection dir,
                                 Packet<?> packet,
                                 CallbackInfoReturnable<Integer> cir)
            throws Exception
    {
        if (packet instanceof CustomPacket)
        {
            cir.setReturnValue(((CustomPacket) packet).getId());
        }
    }

}
