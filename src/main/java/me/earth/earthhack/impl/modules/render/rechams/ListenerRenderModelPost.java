package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderModelPost extends ModuleListener<Chams, ModelRenderEvent.Post>
{

    public ListenerRenderModelPost(Chams module)
    {
        super(module, ModelRenderEvent.Post.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Post event)
    {
        module.getModeFromEntity(event.getEntity()).renderPost(event, module);
    }

}
