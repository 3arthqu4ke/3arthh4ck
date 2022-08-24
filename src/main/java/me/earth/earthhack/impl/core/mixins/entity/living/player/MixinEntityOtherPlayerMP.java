package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.entity.IEntityOtherPlayerMP;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityOtherPlayerMP.class)
public abstract class MixinEntityOtherPlayerMP extends MixinAbstractClientPlayer
        implements IEntityOtherPlayerMP
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);

    private float theYaw;
    private float thePitch;

    @Override
    public boolean attackEntitySuper(DamageSource source, float amount)
    {
        return super.attackEntityFrom(source, amount);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    public void attackEntityFromHook(DamageSource source,
                                      float amount,
                                      CallbackInfoReturnable<Boolean> cir)
    {
        if (this.shouldAttackSuper())
        {
            cir.setReturnValue(this.returnFromSuperAttack(source, amount));
        }
    }

    @Inject(
        method = "setPositionAndRotationDirect",
        at = @At("RETURN"))
    public void setPositionAndRotationDirectHook(double x,
                                                  double y,
                                                  double z,
                                                  float yaw,
                                                  float pitch,
                                                  int posRotationIncrements,
                                                  boolean teleport,
                                                  CallbackInfo ci)
    {
        if (NOINTERP.isEnabled())
        {
            NoInterp.handleNoInterp(NOINTERP.get(),
                                    Entity.class.cast(this),
                                    posRotationIncrements,
                                    x,
                                    y,
                                    z,
                                    yaw,
                                    pitch);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void onLivingUpdateHead(CallbackInfo ci)
    {
        theYaw   = this.rotationYaw;
        thePitch = this.rotationPitch;
    }

    @Redirect(
        method = "onLivingUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityOtherPlayerMP;setRotation(FF)V"))
    public void setRotationHook(EntityOtherPlayerMP entityOtherPlayerMP,
                                 float yaw,
                                 float pitch)
    {
        if (SPECTATE.isEnabled()
                && SPECTATE.get().shouldTurn()
                && entityOtherPlayerMP.equals(SPECTATE.get().getRender()))
        {
            this.setRotation(theYaw, thePitch);
            return;
        }

        this.setRotation(yaw, pitch);
    }

}
