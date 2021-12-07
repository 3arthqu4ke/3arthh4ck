package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.util.MovementInput;

final class ListenerInput extends ModuleListener<NoSlowDown, MovementInputEvent>
{
    public ListenerInput(NoSlowDown module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        MovementInput input = event.getInput();
        if (module.items.getValue()
                && module.input.getValue()
                && input == mc.player.movementInput
                && mc.player.isHandActive()
                && !mc.player.isRiding())
        {
            input.moveStrafe /= 0.2F;
            input.moveForward /= 0.2F;
        }
    }

}
