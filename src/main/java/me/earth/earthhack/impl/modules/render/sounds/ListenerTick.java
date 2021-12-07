package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends ModuleListener<Sounds, TickEvent>
{
    public ListenerTick(Sounds module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        module.sounds.entrySet().removeIf(e ->
                System.currentTimeMillis() - e.getValue()
                        > module.remove.getValue());
    }

}
