package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory
{
    private static final ModuleCache<XCarry> XCARRY =
        Caches.getModule(XCarry.class);

    @Inject(
        method = "onGuiClosed",
        at = @At("HEAD"),
        cancellable = true)
    public void onGuiClosedHook(CallbackInfo info)
    {
        if (XCARRY.isEnabled())
        {
            Keyboard.enableRepeatEvents(false);
            info.cancel();
        }
    }

}
