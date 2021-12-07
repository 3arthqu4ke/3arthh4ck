package me.earth.earthhack.impl.core.mixins.item;

import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFood.class)
public interface IITemFood
{
    @Accessor("potionId")
    PotionEffect getPotionId();
}
