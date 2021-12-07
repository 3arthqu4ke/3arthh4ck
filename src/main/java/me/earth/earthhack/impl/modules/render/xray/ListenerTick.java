package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;

final class ListenerTick extends ModuleListener<XRay, TickEvent>
{
    private int lastOpacity = 0;

    public ListenerTick(XRay module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.getMode() == XrayMode.Opacity)
        {
            if (lastOpacity != module.getOpacity())
            {
                module.loadRenderers();
                lastOpacity = module.getOpacity();
            }

            mc.gameSettings.gammaSetting = 11.0f;
        }
    }

}
