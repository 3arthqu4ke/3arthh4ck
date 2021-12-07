package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities extends ModuleListener<PistonAura,
                                    PacketEvent.Receive<SPacketDestroyEntities>>
{
    public ListenerDestroyEntities(PistonAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event)
    {
        if (module.destroyEntities.getValue())
        {
            for (int id : event.getPacket().getEntityIDs())
            {
                if (id == module.entityId)
                {
                    mc.addScheduledTask(() ->
                    {
                        if (module.current != null)
                        {
                            module.current.setValid(false);
                        }
                    });
                }
            }
        }
    }

}
