package me.earth.earthhack.impl.core.mixins.item;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemExpBottle.class)
public abstract class MixinItemExpBottle
{
    private static final ModuleCache<ExpTweaks> EXP_TWEAKS =
            Caches.getModule(ExpTweaks.class);
    private static final ModuleCache<AutoArmor> AUTO_ARMOR =
            Caches.getModule(AutoArmor.class);

    @Redirect(
        method = "onItemRightClick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void onItemRightClickHook(ItemStack stack, int quantity)
    {
        if (!EXP_TWEAKS.returnIfPresent(ExpTweaks::cancelShrink, false)
                && !AUTO_ARMOR.returnIfPresent(AutoArmor::isBlockingMending, false))
        {
            stack.shrink(quantity);
        }
    }

}
