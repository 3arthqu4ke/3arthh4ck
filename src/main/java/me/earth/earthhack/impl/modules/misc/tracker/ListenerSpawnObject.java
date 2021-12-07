package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<Tracker, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(Tracker module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        SPacketSpawnObject p = event.getPacket();
        if (mc.world == null || mc.player == null)
        {
            return;
        }

        if (p.getType() == 51)
        {
            BlockPos pos = new BlockPos(p.getX(), p.getY(), p.getZ());
            if (!module.placed.remove(pos))
            {
                module.crystals.incrementAndGet();
            }
        }
        else if (p.getType() == 75)
        {
            if (module.awaitingExp.get() > 0)
            {
                if (mc.player.getDistanceSq(p.getX(), p.getY(), p.getZ()) < 16)
                {
                    module.awaitingExp.decrementAndGet();
                }
                else
                {
                    module.exp.incrementAndGet();
                }
            }
            else
            {
                module.exp.incrementAndGet();
            }
        }
    }

}
