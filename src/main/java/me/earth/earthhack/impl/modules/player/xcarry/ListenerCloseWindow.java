package me.earth.earthhack.impl.modules.player.xcarry;

import me.earth.earthhack.impl.core.mixins.network.client.ICPacketCloseWindow;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketCloseWindow;

final class ListenerCloseWindow extends
        ModuleListener<XCarry, PacketEvent.Send<CPacketCloseWindow>>
{
    public ListenerCloseWindow(XCarry module)
    {
        super(module, PacketEvent.Send.class, CPacketCloseWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketCloseWindow> event)
    {
        ICPacketCloseWindow packet = (ICPacketCloseWindow) event.getPacket();
        if (packet.getWindowId() == mc.player.inventoryContainer.windowId)
        {
            event.setCancelled(true);
        }
    }

}
