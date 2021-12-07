package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketResourcePackStatus;

final class ListenerResources
        extends ModuleListener<PingSpoof,
            PacketEvent.Send<CPacketResourcePackStatus>>
{
    private static final ModuleCache<PingBypass> PINGBYPASS =
            Caches.getModule(PingBypass.class);

    public ListenerResources(PingSpoof module)
    {
        super(module, PacketEvent.Send.class, CPacketResourcePackStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketResourcePackStatus> event)
    {
        if (!PINGBYPASS.isEnabled() && module.resources.getValue())
        {
            module.onPacket(event.getPacket());
            event.setCancelled(true);
        }
    }

}
