package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends ModuleListener<EntityControl, TickEvent>
{
    public ListenerTick(EntityControl module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe() && module.jumpHeight.getValue() > 0)
        {
            ((IEntityPlayerSP) mc.player).setHorseJumpPower(1.0f);
        }
    }

}
