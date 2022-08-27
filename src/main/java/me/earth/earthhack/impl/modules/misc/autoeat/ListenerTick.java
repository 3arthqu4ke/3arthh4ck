package me.earth.earthhack.impl.modules.misc.autoeat;

import me.earth.earthhack.impl.core.mixins.item.IITemFood;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import static me.earth.earthhack.impl.util.minecraft.InventoryUtil.findInHotbar;

final class ListenerTick extends ModuleListener<AutoEat, TickEvent>
{
    public ListenerTick(AutoEat module)
    {
        super(module, TickEvent.class, 11);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe())
        {
            module.lastSlot = -1;
            module.isEating = false;
            module.force    = false;
            module.server   = false;
            return;
        }

        module.force = module.always.getValue() || module.server;
        if (mc.player.getFoodStats().getFoodLevel() > module.hunger.getValue()
            && (!module.health.getValue()
                || !enemyCheck()
                || Managers.SAFETY.isSafe()
                    && EntityUtil.getHealth(mc.player,
                                    module.calcWithAbsorption.getValue())
                        > module.safeHealth.getValue()
                || !Managers.SAFETY.isSafe()
                    && EntityUtil.getHealth(mc.player,
                                    module.calcWithAbsorption.getValue())
                        > module.unsafeHealth.getValue())
            && (!module.absorption.getValue()
                || mc.player.getAbsorptionAmount()
                    > module.absorptionAmount.getValue())
            && !module.force)
        {
            if (module.isEating)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                              () -> InventoryUtil.switchTo(module.lastSlot));
                module.reset();
            }

            return;
        }

        int slot = findInHotbar(s -> s.getItem() instanceof ItemFood
                && !hasBadEffect((IITemFood) s.getItem())
                && (!(s.getItem() instanceof ItemFishFood
                        && ItemFishFood.FishType.byItemStack(s)
                                == ItemFishFood.FishType.PUFFERFISH)));
        if (slot == -1)
        {
            ModuleUtil.sendMessage(module, TextColor.RED
                                            + "No food found in your hotbar!");
            return;
        }

        if (module.lastSlot == -1)
        {
            module.lastSlot = mc.player.inventory.currentItem;
        }

        Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                      () -> InventoryUtil.switchTo(slot));
        // SPacketEntityMetadata
        // TODO: mode packets???
        //  need to rebuild Minecraft eating behaviour for that
        //  start with CPacketPlayerTryUseItem and send it again
        //  when server ends our eating With SPacketDataManagerThing?
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),
                                   true);
        module.isEating  = true;
    }

    private boolean enemyCheck()
    {
        if (module.enemyRange.getValue() != 0.0f)
        {
            EntityPlayer entity = EntityUtil.getClosestEnemy();
            return entity != null
                    && entity.getDistanceSq(RotationUtil.getRotationPlayer())
                        < MathUtil.square(module.enemyRange.getValue());
        }

        return false;
    }

    private boolean hasBadEffect(IITemFood itemFood)
    {
        PotionEffect effect = itemFood.getPotionId();
        if (effect != null)
        {
            for (Potion p : Potion.REGISTRY)
            {
                if (p.isBadEffect() && p.equals(effect.getPotion()))
                {
                    return true;
                }
            }
        }

        return false;
    }

}
