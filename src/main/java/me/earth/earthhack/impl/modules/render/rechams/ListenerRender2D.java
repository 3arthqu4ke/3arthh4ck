package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerRender2D extends ModuleListener<ReChams, Render2DEvent>
{

    public ListenerRender2D(ReChams module)
    {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event)
    {
        for (ChamsMode mode : module.getAllCurrentModes())
        {
            mode.render2D(event, module);
        }
    }

}
