package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketUseEntity;

final class ListenerAttack extends
        ModuleListener<Spectate, PacketEvent.Send<CPacketUseEntity>>
{
    public ListenerAttack(Spectate module)
    {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event)
    {
        if (((ICPacketUseEntity) event.getPacket()).getEntityID()
                == mc.player.getEntityId())
        {
            event.setCancelled(true);
        }
    }

}
