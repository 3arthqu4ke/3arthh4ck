package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerUpdate extends ModuleListener<NoMove, UpdateEvent>
{
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);

    public ListenerUpdate(NoMove module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.mode.getValue() == StaticMode.NoVoid)
        {
            if (!mc.player.noClip
                    && mc.player.posY <= module.height.getValue()
                    && !PACKET_FLY.isEnabled())
            {
                final RayTraceResult trace = mc.world.rayTraceBlocks(
                        mc.player.getPositionVector(),
                        new Vec3d(mc.player.posX, 0, mc.player.posZ),
                        false,
                        false,
                        false);

                if (trace == null
                        || trace.typeOfHit != RayTraceResult.Type.BLOCK)
                {
                    if (module.timer.getValue())
                    {
                        Managers.TIMER.setTimer(0.1f);
                    }
                    else
                    {
                        mc.player.setVelocity(0, 0, 0);
                        if (mc.player.getRidingEntity() != null)
                        {
                            mc.player.getRidingEntity().setVelocity(0, 0, 0);
                        }
                    }
                }
            }
        }
    }

}
