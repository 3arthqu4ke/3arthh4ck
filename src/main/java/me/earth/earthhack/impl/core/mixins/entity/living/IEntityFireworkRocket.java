package me.earth.earthhack.impl.core.mixins.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFireworkRocket.class)
public interface IEntityFireworkRocket {

    @Accessor("boostedEntity")
    EntityLivingBase getBoostedEntity();

}
