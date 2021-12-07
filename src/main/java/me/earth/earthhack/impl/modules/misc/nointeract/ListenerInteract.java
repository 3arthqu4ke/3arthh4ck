package me.earth.earthhack.impl.modules.misc.nointeract;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.block.state.IBlockState;

final class ListenerInteract extends
        ModuleListener<NoInteract, ClickBlockEvent.Right>
{
    public ListenerInteract(NoInteract module)
    {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event)
    {
        if (module.sneak.getValue() && Managers.ACTION.isSneaking())
        {
            return;
        }

        IBlockState state = mc.world.getBlockState(event.getPos());
        if (module.isValid(state.getBlock().getLocalizedName()))
        {
            event.setCancelled(true);
        }
    }

}
