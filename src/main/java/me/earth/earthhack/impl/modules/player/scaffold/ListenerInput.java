package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerInput extends ModuleListener<Scaffold, MovementInputEvent>
{
    public ListenerInput(Scaffold module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (module.down.getValue()
                && module.fastSneak.getValue()
                && mc.gameSettings.keyBindSneak.isKeyDown()
                && !mc.gameSettings.keyBindJump.isKeyDown())
        {
            event.getInput().sneak = false;
            event.setCancelled(true);
        }
    }

}
