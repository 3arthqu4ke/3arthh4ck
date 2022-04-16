package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderEntityPost extends ModuleListener<ReChams, RenderEntityEvent.Post>
{

    public ListenerRenderEntityPost(ReChams module)
    {
        super(module, RenderEntityEvent.Post.class);
    }

    @Override
    public void invoke(RenderEntityEvent.Post event)
    {
        module.getModeFromEntity(event.getEntity()).renderEntityPost(event, module);
    }

}