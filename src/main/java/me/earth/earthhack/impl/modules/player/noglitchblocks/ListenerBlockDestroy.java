package me.earth.earthhack.impl.modules.player.noglitchblocks;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerBlockDestroy extends
        ModuleListener<NoGlitchBlocks, BlockDestroyEvent>
{
    public ListenerBlockDestroy(NoGlitchBlocks module)
    {
        super(module, BlockDestroyEvent.class, 1000);
    }

    @Override
    public void invoke(BlockDestroyEvent event)
    {
        if (module.crack.getValue() && event.getStage() == Stage.PRE)
        {
            event.setCancelled(true);
        }
    }

}
