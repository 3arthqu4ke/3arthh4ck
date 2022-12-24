package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;

final class ListenerLiquidJump extends ModuleListener<Jesus, LiquidJumpEvent>
{
    public ListenerLiquidJump(Jesus module)
    {
        super(module, LiquidJumpEvent.class);
    }

    @Override
    public void invoke(LiquidJumpEvent event)
    {
        Entity entity = PositionUtil.getPositionEntity();
        if (entity != null
            && entity.equals(event.getEntity())
            && (entity.isInWater()
                    || entity.isInLava())
            && (entity.motionY == 0.1
                    || entity.motionY == 0.5))
        {
            event.setCancelled(true);
        }
    }

}
