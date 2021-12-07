package me.earth.earthhack.impl.modules.misc.spammer;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerUpdate extends ModuleListener<Spammer, UpdateEvent>
{
    public ListenerUpdate(Spammer module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.timer.passed(module.delay.getValue() * 1000))
        {
            mc.player.sendChatMessage((module.greenText.getValue() ? ">" : "")
                    + module.getSuffixedMessage());

            if (module.autoOff.getValue())
            {
                module.disable();
            }

            module.timer.reset();
        }
    }

}
