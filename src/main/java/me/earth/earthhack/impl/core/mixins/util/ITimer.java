package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface ITimer
{
    @Accessor(value = "tickLength")
    void setTickLength(float length);
}
