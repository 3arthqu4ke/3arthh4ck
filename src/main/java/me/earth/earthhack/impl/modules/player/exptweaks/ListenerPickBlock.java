package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerPickBlock
        extends ModuleListener<ExpTweaks, ClickMiddleEvent>
{
    public ListenerPickBlock(ExpTweaks module)
    {
        super(module, ClickMiddleEvent.class);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (module.pickBlock.getValue())
        {
            mayCancel(module, event);
        }
    }

    public static void mayCancel(ExpTweaks module, Event event) {
        if (module.middleClickExp.getValue()
            && module.mceBind.getValue().getKey() == -1
            && !(event instanceof ClickMiddleEvent
                    && ((ClickMiddleEvent) event).isModuleCancelled()
                || event.isCancelled())
            && !module.isWasting())
        {
            int slot = InventoryUtil.findHotbarItem(Items.EXPERIENCE_BOTTLE);
            if (slot != -1
                && slot != -2
                && slot != mc.player.inventory.currentItem)
            {
                event.setCancelled(true);
            }
        }
    }

}
