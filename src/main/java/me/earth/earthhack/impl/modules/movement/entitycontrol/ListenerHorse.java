package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.event.events.movement.HorseEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerHorse extends ModuleListener<EntityControl, HorseEvent>
{
    public ListenerHorse(EntityControl module)
    {
        super(module, HorseEvent.class);
    }

    @Override
    public void invoke(HorseEvent event)
    {
        event.setJumpHeight(module.jumpHeight.getValue());
    }

}
