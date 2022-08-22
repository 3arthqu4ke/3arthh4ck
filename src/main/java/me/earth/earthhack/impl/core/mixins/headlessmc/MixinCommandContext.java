package me.earth.earthhack.impl.core.mixins.headlessmc;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "me.earth.headlessmc.command.CommandContextImpl", remap = false)
public abstract class MixinCommandContext {
    @Dynamic
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    public void executeHook(String message, CallbackInfo ci) {
        if (message.startsWith(Commands.getPrefix())) {
            Managers.COMMANDS.applyCommand(message);
            ci.cancel();
        }
    }

}
