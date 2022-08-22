package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.movement.ControlEvent;
import me.earth.earthhack.impl.event.events.movement.HorseEvent;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends MixinEntityLivingBase
{
    @Inject(
        method = "getHorseJumpStrength",
        at = @At("HEAD"),
        cancellable = true)
    public void getHorseJumpStrengthHook(CallbackInfoReturnable<Double> info)
    {
        HorseEvent event = new HorseEvent();
        Bus.EVENT_BUS.post(event);

        if (event.getJumpHeight() != 0.0D)
        {
            info.setReturnValue(event.getJumpHeight());
        }
    }

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

    @Inject(
        method = "isHorseSaddled",
        at = @At("HEAD"),
        cancellable = true)
    public void isHorseSaddledHook(CallbackInfoReturnable<Boolean> info)
    {
        ControlEvent event = new ControlEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.setReturnValue(true);
        }
    }

}
