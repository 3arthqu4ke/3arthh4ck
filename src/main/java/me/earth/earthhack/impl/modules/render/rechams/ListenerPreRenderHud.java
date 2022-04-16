package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.PreRenderHandEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerPreRenderHud extends ModuleListener<ReChams, PreRenderHandEvent>
{

    public ListenerPreRenderHud(ReChams module)
    {
        super(module, PreRenderHandEvent.class);
    }

    @Override
    public void invoke(PreRenderHandEvent event)
    {
        for (ChamsMode mode : module.getAllCurrentModes())
        {
            mode.renderHud(event, module);
        }
    }

}
