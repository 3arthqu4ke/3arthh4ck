package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.sounds.util.CustomSound;
import net.minecraft.network.play.server.SPacketCustomSound;

final class ListenerCustomSound extends
        ModuleListener<Sounds, PacketEvent.Receive<SPacketCustomSound>>
{
    public ListenerCustomSound(Sounds module)
    {
        super(module, PacketEvent.Receive.class, SPacketCustomSound.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomSound> event)
    {
        boolean cancelled = event.isCancelled();
        if (module.client.getValue()
            || !module.custom.getValue()
            || cancelled && !module.cancelled.getValue()
            || !module.isValid(event.getPacket().getSoundName()))
        {
            return;
        }

        SPacketCustomSound packet = event.getPacket();
        String s = packet.getSoundName();
        module.sounds.put(new CustomSound(packet.getX(),
                                          packet.getY(),
                                          packet.getZ(),
                                          (cancelled ? "Cancelled: " : "") + s),
                          System.currentTimeMillis());
    }

}
