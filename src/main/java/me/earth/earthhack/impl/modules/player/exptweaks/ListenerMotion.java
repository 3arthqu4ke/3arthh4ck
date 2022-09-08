package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;

final class ListenerMotion extends ModuleListener<ExpTweaks, MotionUpdateEvent>
{
    public ListenerMotion(ExpTweaks module)
    {
        super(module, MotionUpdateEvent.class, 1000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            if (module.feetExp.getValue()
                    && (InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE)
                            && Mouse.isButtonDown(1)
                        || module.isMiddleClick()))
            {
                // maybe look a bit into movement direction?
                event.setPitch(90.0f);
            }
        }
        else if (module.isMiddleClick()
                && !(module.wasteStop.getValue() && module.isWasting())
                && (module.whileEating.getValue()
                    || !(mc.player.getActiveItemStack().getItem()
                                                instanceof ItemFood)))
        {
            int slot = InventoryUtil.findHotbarItem(Items.EXPERIENCE_BOTTLE);
            if (slot != -1)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    int lastSlot = mc.player.inventory.currentItem;
                    boolean silent = module.silent.getValue()
                        && (!module.silentOnlyWhenUsing.getValue()
                            || mc.gameSettings.keyBindUseItem.isKeyDown());
                    if (silent)
                    {
                        module.isMiddleClick = true;
                    }

                    InventoryUtil.switchTo(slot);

                    mc.playerController.processRightClick(
                            mc.player,
                            mc.world,
                            InventoryUtil.getHand(slot));

                    if (silent)
                    {
                        InventoryUtil.switchTo(lastSlot);
                        module.isMiddleClick = false;
                        module.lastSlot = -1;
                    }
                    else if (lastSlot != slot)
                    {
                        module.lastSlot = lastSlot;
                    }
                });
            }
            else if (module.lastSlot != -1)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    InventoryUtil.switchTo(module.lastSlot);
                    module.lastSlot = -1;
                });
            }
        }
        else if (module.lastSlot != -1)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                InventoryUtil.switchTo(module.lastSlot);
                module.lastSlot = -1;
            });
        }
    }

}
