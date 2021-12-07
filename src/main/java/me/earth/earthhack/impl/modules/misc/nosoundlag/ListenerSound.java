package me.earth.earthhack.impl.modules.misc.nosoundlag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketSoundEffect;

final class ListenerSound extends
        ModuleListener<NoSoundLag, PacketEvent.Receive<SPacketSoundEffect>>
{
    public ListenerSound(NoSoundLag module)
    {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event)
    {
        if (module.sounds.getValue()
                && NoSoundLag.SOUNDS.contains(event.getPacket().getSound()))
        {
            event.setCancelled(true);
        }
    }

}
