package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerRender3D extends ModuleListener<Chams, Render3DEvent>
{

    public ListenerRender3D(Chams module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        for (ChamsMode mode : module.getAllCurrentModes())
        {
            mode.render3D(event, module);
        }
    }

}
