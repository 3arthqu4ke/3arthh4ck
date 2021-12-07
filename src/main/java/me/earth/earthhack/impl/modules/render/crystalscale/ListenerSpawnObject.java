package me.earth.earthhack.impl.modules.render.crystalscale;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.network.play.server.SPacketSpawnObject;

final class ListenerSpawnObject extends
        ModuleListener<CrystalScale, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(CrystalScale module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (event.getPacket().getType() == 51)
        {
            module.scaleMap.put(event.getPacket().getEntityID(),
                    new TimeAnimation(
                            module.time.getValue(), 0.1f,
                            module.scale.getValue(),
                            false,
                            AnimationMode.LINEAR));
        }
    }

}
