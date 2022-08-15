package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityVelocity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
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
                    if (PingBypass.isConnected()
                        && module.fixPingBypass.getValue())
                    {
                        event.setPingBypassCancelled(true);
                        SPacketEntityVelocity toClient =
                            new SPacketEntityVelocity(velocity.getEntityID(),
                                                      velocity.getX(),
                                                      velocity.getY(),
                                                      velocity.getZ());
                        // cause the * 8000.0D stuff
                        ISPacketEntityVelocity iToClient =
                            (ISPacketEntityVelocity) toClient;
                        iToClient.setX(velocity.getX());
                        iToClient.setY(velocity.getY());
                        iToClient.setZ(velocity.getZ());
                        PingBypass.sendPacket(toClient);
                    }

                    velocity.setX((int) (velocity.getX()
                            * module.horizontal.getValue()));
                    velocity.setY((int) (velocity.getY()
                            * module.vertical.getValue()));
                    velocity.setZ((int) (velocity.getZ()
                            * module.horizontal.getValue()));
                }
            }
        }
    }

}
