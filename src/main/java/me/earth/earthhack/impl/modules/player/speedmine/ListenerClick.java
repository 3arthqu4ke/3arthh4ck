package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;

final class ListenerClick extends ModuleListener<Speedmine, ClickBlockEvent>
{
    public ListenerClick(Speedmine module)
    {
        super(module, ClickBlockEvent.class);
    }

    @Override
    public void invoke(ClickBlockEvent event)
    {
        if (!PlayerUtil.isCreative(mc.player)
                && (module.noReset.getValue()
                    || module.mode.getValue() == MineMode.Reset)
                && ((IPlayerControllerMP) mc.playerController)
                                                .getCurBlockDamageMP() > 0.1f)
        {
            ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(true);
        }

        if (module.cancelClick.getValue()
            && (module.mode.getValue() == MineMode.Smart
                || module.mode.getValue() == MineMode.Fast))
        {
            event.setCancelled(true);
        }
    }

}
