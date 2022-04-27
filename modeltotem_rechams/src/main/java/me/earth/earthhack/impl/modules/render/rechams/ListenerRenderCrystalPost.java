package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderCrystalPost extends ModuleListener<ReChams, CrystalRenderEvent.Post>
{

    public ListenerRenderCrystalPost(ReChams module)
    {
        super(module, CrystalRenderEvent.Post.class);
    }

    @Override
    public void invoke(CrystalRenderEvent.Post event)
    {
        module.getModeFromEntity(event.getEntity()).renderCrystalPost(event, module);
    }

}