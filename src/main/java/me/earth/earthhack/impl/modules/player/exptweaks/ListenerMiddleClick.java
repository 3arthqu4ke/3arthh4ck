package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerMiddleClick
        extends ModuleListener<ExpTweaks, ClickMiddleEvent>
{
    public ListenerMiddleClick(ExpTweaks module)
    {
        super(module, ClickMiddleEvent.class);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (module.middleClickExp.getValue()
                && module.mceBind.getValue().getKey() == -1
                && !(event.isModuleCancelled() || event.isCancelled())
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
