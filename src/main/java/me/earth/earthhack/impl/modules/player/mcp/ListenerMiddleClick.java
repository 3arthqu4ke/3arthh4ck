package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;

final class ListenerMiddleClick extends
        ModuleListener<MiddleClickPearl, ClickMiddleEvent>
{
    protected ListenerMiddleClick(MiddleClickPearl module)
    {
        super(module, ClickMiddleEvent.class, 11);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (!event.isModuleCancelled() && !event.isCancelled())
        {
            if (InventoryUtil.findHotbarItem(Items.ENDER_PEARL) == -1)
            {
                return;
            }

            if (!module.prioritizeMCF())
            {
                if (module.cancelBlock.getValue())
                {
                    event.setCancelled(true);
                }
            }
            else
            {
                if (module.cancelMCF.getValue())
                {
                    event.setModuleCancelled(true);
                }
                else
                {
                    return;
                }
            }

            module.runnable = () ->
            {
                int slot = InventoryUtil.findHotbarItem(Items.ENDER_PEARL);
                if (slot == -1)
                {
                    return;
                }

                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    int lastSlot = mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);

                    mc.playerController.processRightClick(
                            mc.player, mc.world, InventoryUtil.getHand(slot));

                    InventoryUtil.switchTo(lastSlot);
                });
            };

            if (Managers.ROTATION.getServerPitch() == mc.player.rotationPitch
                && Managers.ROTATION.getServerYaw() == mc.player.rotationYaw)
            {
                module.runnable.run();
                module.runnable = null;
            }
        }
    }

}
