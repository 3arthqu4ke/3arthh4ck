package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

final class ListenerSpawnObject extends ModuleListener<Trails, PacketEvent.Receive<SPacketSpawnObject>> {

    public ListenerSpawnObject(Trails module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if ((module.pearls.getValue() && event.getPacket().getType() == 65)
                || (module.arrows.getValue() && event.getPacket().getType() == 60)
                || (module.snowballs.getValue() && event.getPacket().getType() == 61)) {
            Earthhack.getLogger().info(event.getPacket().getEntityID());
            TimeAnimation animation = new TimeAnimation(module.time.getValue() * 1000, 0, module.color.getAlpha(), false, AnimationMode.LINEAR);
            animation.stop();
            module.ids.put(event.getPacket().getEntityID(), animation);
            module.traceLists.put(event.getPacket().getEntityID(), new ArrayList<>());
            module.traces.put(event.getPacket().getEntityID(), new Trace(0,
                    null,
                    mc.world.provider.getDimensionType(),
                    new Vec3d(event.getPacket().getX(), event.getPacket().getY(), event.getPacket().getZ()),
                    new ArrayList<>()));
        }
    }

}
