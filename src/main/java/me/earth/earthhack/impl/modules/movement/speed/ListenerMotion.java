package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMotion extends ModuleListener<Speed, MotionUpdateEvent>
{
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);

    public ListenerMotion(Speed module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (PACKET_FLY.isEnabled() || FREECAM.isEnabled())
        {
            return;
        }

        if (MovementUtil.noMovementKeys())
        {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }

        module.distance = MovementUtil.getDistance2D();
        if (module.mode.getValue() == SpeedMode.OnGround)
        {
            if (module.onGroundStage == 3)
            {
                event.setY(event.getY()
                            + (PositionUtil.isBoxColliding()
                                ? 0.2
                                : 0.4)
                            + MovementUtil.getJumpSpeed());
            }
        }
    }

}
