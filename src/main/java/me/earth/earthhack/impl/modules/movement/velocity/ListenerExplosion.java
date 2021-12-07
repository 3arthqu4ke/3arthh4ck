package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketExplosion;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion extends
        ModuleListener<Velocity, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000000,
                SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (module.explosions.getValue())
        {
            ISPacketExplosion explosion = (ISPacketExplosion) event.getPacket();
            explosion.setX(explosion.getX() * module.horizontal.getValue());
            explosion.setY(explosion.getY() * module.horizontal.getValue());
            explosion.setZ(explosion.getZ() * module.horizontal.getValue());
        }
    }

}
