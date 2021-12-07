package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;

final class ListenerTick extends ModuleListener<Jesus, TickEvent>
{
    public ListenerTick(Jesus module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.timer.passed(800) && mc.player != null)
        {
            if (module.mode.getValue() == JesusMode.Solid)
            {
                if (mc.player.fallDistance > 3.0F)
                {
                    return;
                }

                if ((mc.player.isInLava()
                        || mc.player.isInWater())
                        && !mc.player.isSneaking())
                {
                    mc.player.motionY = 0.1D;
                    return;
                }

                if (PositionUtil.inLiquid() && !mc.player.isSneaking())
                {
                    mc.player.motionY = 0.1D;
                }
            }
        }
    }

}
