package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderEntity extends ModuleListener<ReChams, RenderEntityEvent.Pre>
{

    public ListenerRenderEntity(ReChams module)
    {
        super(module, RenderEntityEvent.Pre.class);
    }

    @Override
    public void invoke(RenderEntityEvent.Pre event)
    {
        module.getModeFromEntity(event.getEntity()).renderEntity(event, module);
    }

}
