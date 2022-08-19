package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities extends ModuleListener<Trails, PacketEvent.Receive<SPacketDestroyEntities>> {

    public ListenerDestroyEntities(Trails module) {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        for (int id : event.getPacket().getEntityIDs()) {
            if (module.ids.containsKey(id)) {
                module.ids.get(id).play();
            }
        }
    }

}
