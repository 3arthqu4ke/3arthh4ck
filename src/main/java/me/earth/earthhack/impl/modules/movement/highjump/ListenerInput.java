package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerInput extends ModuleListener<HighJump, MovementInputEvent>
{
    public ListenerInput(HighJump module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (module.onlySpecial.getValue()
                && (module.explosions.getValue() || module.velocity.getValue())
                && module.cancelJump.getValue()
                && module.motionY < module.minY.getValue())
        {
            event.getInput().jump = false;
        }
    }

}
