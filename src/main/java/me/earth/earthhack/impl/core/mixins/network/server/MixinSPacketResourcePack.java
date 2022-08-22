package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketResourcePackSend.class)
public abstract class MixinSPacketResourcePack
{
    private static final ModuleCache<Packets> PACKETS =
            Caches.getModule(Packets.class);

    @Inject(method = "readPacketData", at = @At("HEAD"), cancellable = true)
    public void readPacketDataHook(PacketBuffer buf, CallbackInfo ci)
    {
        if (PACKETS.returnIfPresent(Packets::areCCResourcesActive, false))
        {
            // We could carefully check the first bytes of the URL.
            // But since this is only for CC it doesn't really matter.
            buf.readerIndex(buf.writerIndex());
            ci.cancel();
        }
    }

}
