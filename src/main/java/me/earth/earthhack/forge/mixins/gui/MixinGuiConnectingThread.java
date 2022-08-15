package me.earth.earthhack.forge.mixins.gui;

import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

// TODO: same thing for vanilla?
@Mixin(targets = "net.minecraft.client.multiplayer.GuiConnecting$1")
public class MixinGuiConnectingThread {
    @Redirect(
        method = "run()V",
        at = @At(
            value = "NEW",
            target = "net/minecraft/util/text/TextComponentTranslation"))
    private TextComponentTranslation onDisplayGuiScreen(String translationKey, Object... args) {
        PingBypass.disconnect(new TextComponentString(
            "PingBypass disconnected: " + Arrays.toString(args)));
        return new TextComponentTranslation(translationKey, args);
    }

}
