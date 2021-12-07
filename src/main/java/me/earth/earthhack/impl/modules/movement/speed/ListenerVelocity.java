package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity extends
        ModuleListener<Speed, PacketEvent.Receive<SPacketEntityVelocity>>
{
    public ListenerVelocity(Speed module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event)
    {
        SPacketEntityVelocity packet = event.getPacket();
        EntityPlayerSP player = mc.player;
        if (player != null
                && packet.getEntityID() == player.getEntityId()
                && !module.directional.getValue()
                && module.velocity.getValue())
        {
            double speed = Math.sqrt(
                    packet.getMotionX() * packet.getMotionX()
                            + packet.getMotionZ() * packet.getMotionZ())
                    /  8000.0;

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

                    if (mc.player.motionY > 0
                            && module.vertical.getValue() != 0)
                    {
                        mc.player.motionY *= module.vertical.getValue();
                    }
                });
            }
        }
    }

}
