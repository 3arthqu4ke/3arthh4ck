package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

final class ListenerConfirmTransaction extends ModuleListener<Packets,
        PacketEvent.Receive<SPacketConfirmTransaction>>
{
    public ListenerConfirmTransaction(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000,
                SPacketConfirmTransaction.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketConfirmTransaction> event)
    {
        if (!event.isCancelled()
                && module.fastTransactions.getValue()
                && mc.player != null)
        {
            SPacketConfirmTransaction packet = event.getPacket();
            if (!packet.wasAccepted()
                    && (packet.getWindowId() == 0
                            ? mc.player.inventoryContainer
                            : mc.player.openContainer) != null)
            {
                event.setCancelled(true);
                mc.player
                  .connection
                  .sendPacket(
                    new CPacketConfirmTransaction(packet.getWindowId(),
                                                  packet.getActionNumber(),
                                                  true));
            }
        }
    }

}
