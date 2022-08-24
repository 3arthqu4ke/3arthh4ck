package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MovementInputFromOptions.class)
public abstract class MixinMovementInputFromOptions
{
    @Redirect(
        method = "updatePlayerMoveState",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/util/MovementInputFromOptions;sneak:Z",
            ordinal = 1))
    public boolean sneakHook(MovementInputFromOptions input)
    {
        boolean sneak = input.sneak;
        Event event = new MovementInputEvent(input);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            return false;
        }

        return sneak;
    }

}
