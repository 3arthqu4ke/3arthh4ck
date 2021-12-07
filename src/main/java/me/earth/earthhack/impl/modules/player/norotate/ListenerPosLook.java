package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends
        ModuleListener<NoRotate, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    public ListenerPosLook(NoRotate module)
    {
        super(module,
                PacketEvent.Receive.class,
                -5,
                SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        if (module.noForceLook.getValue() && !event.isCancelled())
        {
            event.setCancelled(true);
            if (module.async.getValue())
            {
                PacketUtil.handlePosLook(event.getPacket(), mc.player, true);
            }
            else
            {
                mc.addScheduledTask(() ->
                  PacketUtil.handlePosLook(event.getPacket(), mc.player, true));
            }
        }
    }

}
