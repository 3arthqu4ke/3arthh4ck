package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerBobber extends
        ModuleListener<Velocity, PacketEvent.Receive<SPacketEntityStatus>>
{
    public ListenerBobber(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event)
    {
        if (module.bobbers.getValue())
        {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 31 && !event.isCancelled())
            {
                event.setCancelled(true);
                mc.addScheduledTask(() ->
                {
                    if (mc.getConnection() == null)
                    {
                        return;
                    }

                    Entity entity = packet.getEntity(mc.world);
                    if (entity instanceof EntityFishHook)
                    {
                        EntityFishHook fishHook = (EntityFishHook) entity;
                        if (fishHook.caughtEntity != null
                                && mc.getConnection() != null
                                && !fishHook.caughtEntity.equals(mc.player))
                        {
                            packet.processPacket(mc.getConnection());
                        }
                    }
                    else
                    {
                        packet.processPacket(mc.getConnection());
                    }
                });
            }
        }
    }

}
