package me.earth.earthhack.impl.modules.player.reach;

import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerReach extends ModuleListener<Reach, ReachEvent>
{
    public ListenerReach(Reach module)
    {
        super(module, ReachEvent.class);
    }

    @Override
    public void invoke(ReachEvent event)
    {
        if (mc.getRenderViewEntity() != null
                && mc.world != null
                && mc.playerController != null)
        {
            event.setReach(module.reach.getValue());
            event.setHitBox(module.hitBox.getValue());
            event.setCancelled(true);
        }
    }

}
