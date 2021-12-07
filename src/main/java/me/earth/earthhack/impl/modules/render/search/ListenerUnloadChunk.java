package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerUnloadChunk extends ModuleListener<Search, UnloadChunkEvent>
{
    public ListenerUnloadChunk(Search module)
    {
        super(module, UnloadChunkEvent.class);
    }

    @Override
    public void invoke(UnloadChunkEvent event)
    {
        if (module.noUnloaded.getValue() && mc.world != null)
        {
            module.toRender.keySet().removeIf(p -> !mc.world.isBlockLoaded(p));
        }
    }

}
