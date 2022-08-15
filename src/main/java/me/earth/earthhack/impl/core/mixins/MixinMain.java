package me.earth.earthhack.impl.core.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Main.class)
public abstract class MixinMain {
    @Redirect(
        method = "main",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getSystemTime()J"))
    private static long getSystemTimeHook() {
        if (Boolean.parseBoolean(
            System.getProperty("earthhack.no.random.dev", "false"))) {
            return 1L;
        }

        return Minecraft.getSystemTime();
    }

}
