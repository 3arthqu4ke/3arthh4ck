package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerInput extends ModuleListener<NoAFK, MovementInputEvent>
{
    public ListenerInput(NoAFK module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (module.sneak.getValue())
        {
            if (module.sneak_timer.passed(2000))
            {
                module.sneaking = !module.sneaking;
                module.sneak_timer.reset();
            }

            event.getInput().sneak = module.sneaking;
        }
    }

}
