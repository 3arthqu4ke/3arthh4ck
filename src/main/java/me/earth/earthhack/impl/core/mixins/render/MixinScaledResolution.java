package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScaledResolution.class)
public abstract class MixinScaledResolution
{

    private final ModuleCache<Management> MANAGEMENT =
            Caches.getModule(Management.class);

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow @Final private double scaledWidthD;

    @Shadow @Final private double scaledHeightD;

    @Shadow private int scaleFactor;

    /*@Inject(method = "getScaledWidth", at = @At("RETURN"), cancellable = true)
    public void getScaledWidthHook(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.round(scaledWidth * MANAGEMENT.get().getGuiScale()));
    }

    @Inject(method = "getScaledHeight", at = @At("RETURN"), cancellable = true)
    public void getScaledHeightHook(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.round(scaledHeight * MANAGEMENT.get().getGuiScale()));
    }

    @Inject(method = "getScaledWidth_double", at = @At("RETURN"), cancellable = true)
    public void getScaledWidthDoubleHook(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((double) (scaledWidthD * MANAGEMENT.get().getGuiScale()));
    }

    @Inject(method = "getScaledHeight_double", at = @At("RETURN"), cancellable = true)
    public void getScaledHeightDoubleHook(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((double) (scaledHeightD * MANAGEMENT.get().getGuiScale()));
    }

    @Inject(method = "getScaleFactor", at = @At("RETURN"), cancellable = true)
    public void getScaleFactorHook(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.round(scaleFactor * MANAGEMENT.get().getGuiScale()));
    }*/

}
