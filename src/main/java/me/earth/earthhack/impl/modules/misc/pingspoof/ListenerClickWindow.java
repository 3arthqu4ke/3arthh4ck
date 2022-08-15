package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import net.minecraft.network.play.client.CPacketClickWindow;

final class ListenerClickWindow extends
        ModuleListener<PingSpoof, PacketEvent.Post<CPacketClickWindow>>
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public ListenerClickWindow(PingSpoof module)
    {
        super(module, PacketEvent.Post.class, CPacketClickWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketClickWindow> event)
    {
        if (module.transactions.getValue() && !PINGBYPASS.isEnabled())
        {
            module.transactionIDs.add(event.getPacket().getActionNumber());
        }
    }

}
