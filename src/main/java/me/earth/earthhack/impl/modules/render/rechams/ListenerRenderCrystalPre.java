package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderCrystalPre extends ModuleListener<ReChams, CrystalRenderEvent.Pre>
{

    public ListenerRenderCrystalPre(ReChams module)
    {
        super(module, CrystalRenderEvent.Pre.class);
    }

    @Override
    public void invoke(CrystalRenderEvent.Pre event)
    {
        module.getModeFromEntity(event.getEntity()).renderCrystalPre(event, module);
    }

}
