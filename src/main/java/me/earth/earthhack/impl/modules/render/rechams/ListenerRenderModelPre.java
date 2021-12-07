package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderModelPre extends ModuleListener<Chams, ModelRenderEvent.Pre>
{

    public ListenerRenderModelPre(Chams module)
    {
        super(module, ModelRenderEvent.Pre.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Pre event)
    {
        module.getModeFromEntity(event.getEntity()).renderPre(event, module);
    }

}
