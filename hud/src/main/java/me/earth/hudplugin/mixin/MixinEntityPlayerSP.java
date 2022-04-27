package me.earth.hudplugin.mixin;

import me.earth.hudplugin.HUDModule;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP
{
    @Inject(method = "Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V", at = @At("HEAD"))
    public void getBrand(String brand, CallbackInfo callbackInfo) {
        HUDModule.theServerBrand = brand;
    }
}
