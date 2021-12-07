package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderCrystalPre extends ModuleListener<Chams, CrystalRenderEvent.Pre>
{

    public ListenerRenderCrystalPre(Chams module)
    {
        super(module, CrystalRenderEvent.Pre.class);
    }

    @Override
    public void invoke(CrystalRenderEvent.Pre event)
    {
        module.getModeFromEntity(event.getEntity()).renderCrystalPre(event, module);
    }

}
