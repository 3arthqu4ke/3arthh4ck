package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;

final class ListenerRightClick extends ModuleListener<BowKiller, RightClickItemEvent>
{

    public ListenerRightClick(BowKiller module)
    {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event)
    {
        if (!mc.player.collidedVertically)
            return;
        if (mc.player.getHeldItem(event.getHand()).getItem() == Items.BOW && module.blockUnder)
        {
            module.cancelling = true;
            module.needsMessage = true;
        }
    }

}
