package me.earth.phobosmainmenu.mixins;

import me.earth.phobosmainmenu.gui.custom.GuiCustomMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);

    @Inject(method = "runTick()V", at = @At("RETURN"))
    private void runTick(CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCustomMainScreen());
        }
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"))
    private void displayGuiScreen(GuiScreen screen, CallbackInfo ci) {
        if (screen instanceof GuiMainMenu) {
            this.displayGuiScreen(new GuiCustomMainScreen());
        }
    }
}
