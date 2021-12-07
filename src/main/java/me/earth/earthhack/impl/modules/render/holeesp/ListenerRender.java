package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerRender extends ModuleListener<HoleESP, Render3DEvent>
{
    public ListenerRender(HoleESP module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        module.onRender3D();
    }

}
