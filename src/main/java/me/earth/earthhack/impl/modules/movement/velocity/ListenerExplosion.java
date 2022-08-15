package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketExplosion;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;

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
            if (PingBypass.isConnected()
                && module.fixPingBypass.getValue())
            {
                SPacketExplosion p = event.getPacket();
                event.setPingBypassCancelled(true);
                PingBypass.sendPacket(
                    new SPacketExplosion(
                        p.getX(), p.getY(), p.getZ(),
                        p.getStrength(), p.getAffectedBlockPositions(),
                        new Vec3d(
                            p.getMotionX(), p.getMotionY(), p.getMotionZ())));
            }

            ISPacketExplosion explosion = (ISPacketExplosion) event.getPacket();
            explosion.setX(explosion.getX() * module.horizontal.getValue());
            explosion.setY(explosion.getY() * module.vertical.getValue());
            explosion.setZ(explosion.getZ() * module.horizontal.getValue());
        }
    }

}
