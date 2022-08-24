package me.earth.earthhack.impl.core.mixins.network;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NettyCompressionDecoder.class)
public abstract class MixinNettyCompressionDecoder
{
    private static final ModuleCache<Packets> PACKETS =
            Caches.getModule(Packets.class);

    @ModifyConstant(
        method = "decode",
        constant = @Constant(intValue = 0x200000))
    public int decodeHook(int threshold)
    {
        return PACKETS.returnIfPresent(Packets::isNoBookBanActive, false)
                    ? Integer.MAX_VALUE
                    : threshold;
    }

}
