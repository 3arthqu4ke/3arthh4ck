package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityBoat.class)
public abstract class MixinEntityBoat extends MixinEntity
{
    private static final ModuleCache<BoatFly> BOAT_FLY =
            Caches.getModule(BoatFly.class);

    @Redirect(
        method = "updateMotion",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/item/EntityBoat.hasNoGravity()Z"))
    public boolean updateMotionHook(EntityBoat boat)
    {
        return boat.hasNoGravity()
                || BOAT_FLY.isEnabled()
                    && BOAT_FLY.returnIfPresent(BoatFly::getGlideSpeed, 0.0001f)
                               .floatValue() == 0.0f;
    }

}
