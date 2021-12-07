package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLiquidJump extends ModuleListener<Jesus, LiquidJumpEvent>
{
    public ListenerLiquidJump(Jesus module)
    {
        super(module, LiquidJumpEvent.class);
    }

    @Override
    public void invoke(LiquidJumpEvent event)
    {
        if (mc.player != null
            && mc.player.equals(event.getEntity())
            && (mc.player.isInWater()
                    || mc.player.isInLava())
            && (mc.player.motionY == 0.1
                    || mc.player.motionY == 0.5))
        {
            event.setCancelled(true);
        }
    }

}
