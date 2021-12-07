package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;

//TODO: THIS!
final class ListenerTick extends ModuleListener<AutoFish, TickEvent>
{
    private static final ModuleCache<AutoEat> AUTOEAT =
            Caches.getModule(AutoEat.class);

    public ListenerTick(AutoFish module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe()
            || AUTOEAT.returnIfPresent(AutoEat::isEating, false))
        {
            return;
        }

        int slot = InventoryUtil.findHotbarItem(Items.FISHING_ROD);
        if (slot == -1)
        {
            // TODO: If we replenish we should wait a bit
            ModuleUtil.disableRed(module,
                                  "No fishing rod found in your hotbar.");
        }

        if (mc.player.inventory.currentItem != slot)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                          () -> InventoryUtil.switchTo(slot));
        }
        else if (module.delayCounter > 0)
        {
            module.delayCounter--;
        }
        else
        {
            EntityFishHook fish = mc.player.fishEntity;
            if (fish == null)
            {
                module.click();
                return;
            }

            if (++module.timeout > 720)
            {
                module.click();
            }

            if (mc.player.fishEntity.caughtEntity != null)
            {
                module.click();
            }

            if (module.splash)
            {
                if (++module.splashTicks >= 4)
                {
                    module.click();
                    module.splash = false;
                }
            }
            else if (module.splashTicks != 0)
            {
                module.splashTicks--;
            }
        }
    }

}
