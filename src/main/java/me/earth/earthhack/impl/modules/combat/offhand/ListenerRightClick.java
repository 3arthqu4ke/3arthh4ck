package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;

final class ListenerRightClick
        extends ModuleListener<Offhand, ClickBlockEvent.Right>
{
    public ListenerRightClick(Offhand module)
    {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event)
    {
        if (module.noOGC.getValue() && event.getHand() == EnumHand.MAIN_HAND)
        {
            Item mainHand = mc.player.getHeldItemMainhand().getItem();
            Item offHand  = mc.player.getHeldItemOffhand().getItem();
            if (mainHand == Items.END_CRYSTAL
                    && offHand == Items.GOLDEN_APPLE
                    && event.getHand() == EnumHand.MAIN_HAND)
            {
                event.setCancelled(true);
                mc.player.setActiveHand(EnumHand.OFF_HAND);
                mc.playerController.processRightClick(mc.player,
                                                      mc.world,
                                                      EnumHand.OFF_HAND);
            }
        }
    }

}
