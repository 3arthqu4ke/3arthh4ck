package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import net.minecraft.network.play.client.CPacketKeepAlive;

final class ListenerKeepAlive extends
        ModuleListener<PingSpoof, PacketEvent.Send<CPacketKeepAlive>>
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public ListenerKeepAlive(PingSpoof module)
    {
        super(module, PacketEvent.Send.class, CPacketKeepAlive.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketKeepAlive> event)
    {
        if (!PINGBYPASS.isEnabled() && module.keepAlive.getValue())
        {
            module.onPacket(event.getPacket());
            event.setCancelled(true);
        }
    }

}
