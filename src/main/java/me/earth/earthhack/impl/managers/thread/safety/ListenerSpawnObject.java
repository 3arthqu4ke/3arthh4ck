package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<SafetyManager,
                PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(SafetyManager manager)
    {
        super(manager, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        SPacketSpawnObject p = event.getPacket();
        if (p.getType() == 51 && mc.player != null)
        {
            if (DamageUtil.calculate(
                    new BlockPos(p.getX(), p.getY(), p.getZ()).down())
                        > module.damage.getValue())
            {
                module.setSafe(false);
            }
        }
    }

}
