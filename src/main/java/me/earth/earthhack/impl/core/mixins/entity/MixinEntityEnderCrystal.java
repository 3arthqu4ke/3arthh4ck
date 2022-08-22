package me.earth.earthhack.impl.core.mixins.entity;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEnderCrystal.class)
public abstract class MixinEntityEnderCrystal extends MixinEntity
{


    @Inject(
        method = "<init>(Lnet/minecraft/world/World;DDD)V",
        at = @At("RETURN"))
    public void initHook(World worldIn,
                          double x,
                          double y,
                          double z,
                          CallbackInfo ci)
    {
        // Since PrevPos x, y, z are 0 in the beginning interpolation
        // can make it look like crystals get teleported on the ESP
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
    }
}
