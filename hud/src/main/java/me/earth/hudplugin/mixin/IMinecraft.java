package me.earth.hudplugin.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public interface IMinecraft
{
    @Accessor("debugFPS")
    static int getDebugFPS()
    {
        throw new IllegalStateException("mixin not implemented");
    }
}
