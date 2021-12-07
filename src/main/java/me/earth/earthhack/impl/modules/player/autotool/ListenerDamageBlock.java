package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.thread.Locks;

final class ListenerDamageBlock extends
        ModuleListener<AutoTool, DamageBlockEvent>
{
    private static final ModuleCache<Speedmine> SPEED_MINE =
            Caches.getModule(Speedmine.class);

    public ListenerDamageBlock(AutoTool module)
    {
        super(module, DamageBlockEvent.class);
    }

    @Override
    public void invoke(DamageBlockEvent event)
    {
        if (MineUtil.canBreak(event.getPos())
                && !mc.player.isCreative()
                && mc.gameSettings.keyBindAttack.isKeyDown()
                && (!SPEED_MINE.isPresent()
                    || !SPEED_MINE.isEnabled()
                    || (SPEED_MINE.get().getMode() == MineMode.Damage
                        || SPEED_MINE.get().getMode() == MineMode.Reset)))
        {
            int slot = MineUtil.findBestTool(event.getPos());
            if (slot != -1)
            {
                if (!module.set)
                {
                    module.lastSlot = mc.player.inventory.currentItem;
                    module.set = true;
                }

                Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                              () -> InventoryUtil.switchTo(slot));
            }
        }
        else if (module.set)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                          () -> InventoryUtil.switchTo(module.lastSlot));
            module.reset();
        }
    }

}
