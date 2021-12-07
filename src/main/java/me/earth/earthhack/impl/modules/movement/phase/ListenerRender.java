package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerRender extends ModuleListener<Phase, Render3DEvent>
{
    public ListenerRender(Phase module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.esp.getValue()
                && module.pos != null
                && !module.clickTimer.passed(750))
        {
            module.renderPos(module.pos);
        }
    }

}
