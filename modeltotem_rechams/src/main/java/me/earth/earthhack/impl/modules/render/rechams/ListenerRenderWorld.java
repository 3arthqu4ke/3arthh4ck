package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.WorldRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerRenderWorld extends ModuleListener<ReChams, WorldRenderEvent>
{

    public ListenerRenderWorld(ReChams module)
    {
        super(module, WorldRenderEvent.class);
    }

    @Override
    public void invoke(WorldRenderEvent event)
    {
        for (ChamsMode mode : module.getAllCurrentModes())
        {
            mode.renderWorld(event, module);
        }
    }

}
