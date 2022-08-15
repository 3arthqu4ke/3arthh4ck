package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker {
    @Inject(method = "updateServerPosition", at = @At("HEAD"))
    private static void updateServerPositionHook(Entity entityIn,
                                                 double x,
                                                 double y,
                                                 double z,
                                                 CallbackInfo ci)
    {
        ((IEntity) entityIn).setOldServerPos(
            entityIn.serverPosX,
            entityIn.serverPosY,
            entityIn.serverPosZ);
    }

}
