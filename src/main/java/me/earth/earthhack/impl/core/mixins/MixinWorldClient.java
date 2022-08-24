package me.earth.earthhack.impl.core.mixins;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.EntityChunkEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient
{
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void constructorHook(CallbackInfo callbackInfo)
    {
        Bus.EVENT_BUS.post(new WorldClientEvent.Load(
                WorldClient.class.cast(this)));
    }

    @ModifyVariable(
        method = "showBarrierParticles(IIIILjava/util/Random;" +
                 "ZLnet/minecraft/util/math/BlockPos" +
                 "$MutableBlockPos;)V",
        at = @At(value = "HEAD"))
    public boolean showBarrierParticlesHook(boolean holdingBarrier)
    {
        return NO_RENDER.returnIfPresent(NoRender::showBarriers, false)
                || holdingBarrier;
    }


    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    public void onEntityAdded(Entity entity, CallbackInfo info) {
        Bus.EVENT_BUS.post(new EntityChunkEvent(
                Stage.PRE,
                entity));
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemoved(Entity entity, CallbackInfo info) {
        Bus.EVENT_BUS.post(new EntityChunkEvent(
                Stage.POST,
                entity));
    }
}
