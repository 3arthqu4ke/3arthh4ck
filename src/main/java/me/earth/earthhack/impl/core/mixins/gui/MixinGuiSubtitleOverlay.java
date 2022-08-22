package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSubtitleOverlay.class)
public abstract class MixinGuiSubtitleOverlay
{

    @Inject(
        method = "renderSubtitles",
        at = @At(value = "HEAD"))
    public void renderSubtitlesHook(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new Render2DEvent());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

}
