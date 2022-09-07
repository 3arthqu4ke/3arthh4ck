package me.earth.backup.mixins;

import me.earth.backup.BackupPlugin;
import me.earth.earthhack.impl.managers.Managers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Managers.class, remap = false)
public class MixinManagers {
    @Inject(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lme/earth/earthhack/impl/managers/client/ModuleManager;load()V",
            shift = At.Shift.BEFORE))
    private static void loadHook(CallbackInfo ci) {
        BackupPlugin.backup();
    }

}
