package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.BeginRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerBeginRender extends ModuleListener<ReChams, BeginRenderEvent>
{

    public ListenerBeginRender(ReChams module)
    {
        super(module, BeginRenderEvent.class);
    }

    @Override
    public void invoke(BeginRenderEvent event)
    {
        for (ChamsMode mode : module.getAllCurrentModes())
        {
            mode.beginRender(event, module);
        }
    }

}
