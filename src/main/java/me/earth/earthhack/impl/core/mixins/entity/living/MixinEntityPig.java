package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.misc.AIEvent;
import me.earth.earthhack.impl.event.events.movement.ControlEvent;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPig.class)
public abstract class MixinEntityPig extends EntityAnimal
{
    public MixinEntityPig(World world)
    {
        super(world);
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

    @Redirect(
        method = "travel",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/passive/EntityAnimal.travel(FFF)V"))
    public void travelHook(EntityAnimal var1,
                            float strafe,
                            float vertical,
                            float forward)
    {
        AIEvent event = new AIEvent();
        Bus.EVENT_BUS.post(event);

        super.travel(strafe, vertical, event.isCancelled() ? 0.0F : forward);
    }

}
