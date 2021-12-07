package me.earth.earthhack.impl.modules.player.blocktweaks;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends ModuleListener<BlockTweaks, TickEvent>
{
    public ListenerTick(BlockTweaks module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        IPlayerControllerMP controller
                = (IPlayerControllerMP) mc.playerController;
        if (controller != null
                && controller.getBlockHitDelay() > module.delay.getValue())
        {
            controller.setBlockHitDelay(module.delay.getValue());
        }
    }

}
