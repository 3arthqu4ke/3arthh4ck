package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityVelocity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerEntityVelocity extends
        ModuleListener<Velocity, PacketEvent.Receive<SPacketEntityVelocity>>
{
    public ListenerEntityVelocity(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000000,
                SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event)
    {
        if (module.knockBack.getValue() && mc.player != null)
        {
            ISPacketEntityVelocity velocity =
                    (ISPacketEntityVelocity) event.getPacket();
            if (velocity.getEntityID() == mc.player.getEntityId())
            {
                if (module.horizontal.getValue() == 0
                        && module.vertical.getValue() == 0)
                {
                    event.setCancelled(true);
                }
                else
                {
                    velocity.setX((int) (velocity.getX()
                            * module.horizontal.getValue()));
                    velocity.setX((int) (velocity.getX()
                            * module.vertical.getValue()));
                    velocity.setX((int) (velocity.getX()
                            * module.horizontal.getValue()));
                }
            }
        }
    }

}
