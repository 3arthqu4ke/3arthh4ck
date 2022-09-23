package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;

final class ListenerTick extends ModuleListener<Jesus, TickEvent>
{
    public ListenerTick(Jesus module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        Entity entity = PositionUtil.getPositionEntity();
        if (module.timer.passed(800) && entity != null)
        {
            if (module.mode.getValue() == JesusMode.Solid)
            {
                if (entity.fallDistance > 3.0F)
                {
                    return;
                }

                if ((entity.isInLava()
                        || entity.isInWater())
                        && !entity.isSneaking())
                {
                    entity.motionY = 0.1D;
                    return;
                }

                if (PositionUtil.inLiquid() && !entity.isSneaking())
                {
                    entity.motionY = 0.1D;
                }
            }
        }
    }

}
