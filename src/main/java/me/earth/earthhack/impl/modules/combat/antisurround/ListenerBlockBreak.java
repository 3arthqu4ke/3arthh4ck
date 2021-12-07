package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerBlockBreak
        extends ModuleListener<AntiSurround, BlockDestroyEvent>
{
    public ListenerBlockBreak(AntiSurround module)
    {
        super(module, BlockDestroyEvent.class);
    }

    @Override
    public void invoke(BlockDestroyEvent event)
    {
        if (module.active.get()
            || !module.normal.getValue()
            || event.isCancelled()
            || event.isUsed()
            || event.getStage() != Stage.PRE
            || mc.player == null
            || module.holdingCheck())
        {
            return;
        }

        event.setUsed(true);
        module.onBlockBreak(event.getPos(),
                            mc.world.playerEntities,
                            mc.world.loadedEntityList);
    }

}
