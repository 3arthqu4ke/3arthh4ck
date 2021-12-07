package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.event.events.misc.AIEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerAI extends ModuleListener<EntityControl, AIEvent>
{
    public ListenerAI(EntityControl module)
    {
        super(module, AIEvent.class);
    }

    @Override
    public void invoke(AIEvent event)
    {
        if (module.noAI.getValue())
        {
            event.setCancelled(true);
        }
    }

}
