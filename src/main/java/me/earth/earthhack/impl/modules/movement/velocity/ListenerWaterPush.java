package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.movement.WaterPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWaterPush extends ModuleListener<Velocity, WaterPushEvent>
{
    public ListenerWaterPush(Velocity module)
    {
        super(module, WaterPushEvent.class);
    }

    @Override
    public void invoke(WaterPushEvent event)
    {
        if (module.water.getValue() && event.getEntity().equals(mc.player))
        {
            event.setCancelled(true);
        }
    }

}
