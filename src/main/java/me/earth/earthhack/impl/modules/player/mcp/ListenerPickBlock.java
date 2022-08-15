package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;

final class ListenerPickBlock extends
        ModuleListener<MiddleClickPearl, ClickMiddleEvent>
{
    public ListenerPickBlock(MiddleClickPearl module)
    {
        super(module, ClickMiddleEvent.class, 11);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (!event.isModuleCancelled()
            && module.pickBlock.getValue()
            && !event.isCancelled())
        {
            module.onClick(event);
        }
    }

}
