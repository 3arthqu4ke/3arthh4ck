package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import org.lwjgl.input.Keyboard;

final class ListenerKeyboard extends ModuleListener<Arrows, KeyboardEvent>
{
    public ListenerKeyboard(Arrows module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (module.cycleButton.getValue().getKey() == Keyboard.getEventKey()
            && event.getEventState())
        {
            module.cycle(false, false);
        }
    }

}
