package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.movement.ControlEvent;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLlama.class)
public abstract class MixinEntityLlama extends MixinAbstractHorse
{
    @Inject(
        method = "canBeSteered",
        at = @At("HEAD"),
        cancellable = true)
    public void canBeSteeredHook(CallbackInfoReturnable<Boolean> info)
    {
        ControlEvent event = new ControlEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.setReturnValue(true);
        }
    }

}
