package me.earth.earthhack.impl.core.mixins.entity.living;

import net.minecraft.entity.passive.EntityWolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityWolf.class)
public interface IEntityWolf
{
    @Accessor(value = "isWet")
    boolean getIsWet();

    @Accessor(value = "isWet")
    void setIsWet(boolean wet);
}
