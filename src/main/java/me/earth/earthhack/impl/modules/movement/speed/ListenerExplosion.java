package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

//TODO: Make this even better
final class ListenerExplosion extends
        ModuleListener<Speed, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(Speed module)
    {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (module.explosions.getValue()
                && MovementUtil.isMoving()
                && Managers.NCP.passed(module.lagTime.getValue()))
        {
            SPacketExplosion packet = event.getPacket();
            BlockPos pos = new BlockPos(packet.getX(),
                                        packet.getY(),
                                        packet.getZ());

            if (mc.player.getDistanceSq(pos) < 100
                    && (!module.directional.getValue()
                        || !MovementUtil.isInMovementDirection(packet.getX(),
                                                               packet.getY(),
                                                               packet.getZ())))
            {
                double speed = Math.sqrt(
                        packet.getMotionX() * packet.getMotionX()
                        + packet.getMotionZ() * packet.getMotionZ());

                module.lastExp = module.expTimer
                                       .passed(module.coolDown.getValue())
                                            ? speed
                                            : (speed - module.lastExp);

                if (module.lastExp > 0)
                {
                    module.expTimer.reset();
                    mc.addScheduledTask(() ->
                    {
                        module.speed +=
                                module.lastExp * module.multiplier.getValue();

                        module.distance +=
                                module.lastExp * module.multiplier.getValue();

                        if (mc.player.motionY > 0)
                        {
                            mc.player.motionY *= module.vertical.getValue();
                        }
                    });
                }
            }
        }
    }

}
