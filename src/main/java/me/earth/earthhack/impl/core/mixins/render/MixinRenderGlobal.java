package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.render.IRenderGlobal;
import me.earth.earthhack.impl.event.events.render.RenderEntityInWorldEvent;
import me.earth.earthhack.impl.event.events.render.RenderSkyEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal implements IRenderGlobal
{
    @Shadow
    private int countEntitiesRendered;
    private static final ModuleCache<XRay>
        XRAY = Caches.getModule(XRay.class);
    private static final ModuleCache<NoRender>
        NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<Ambience> AMBIENCE = Caches.getModule(Ambience.class);

    // For Freecam too?
    @ModifyVariable(
        method = "setupTerrain",
        at = @At("HEAD"))
    public boolean setupTerrainHook(boolean playerSpectator)
    {
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Opacity)
        {
            return true;
        }

        return playerSpectator;
    }

    @Inject(method = "renderWorldBorder", at = @At("HEAD"), cancellable = true)
    public void onRenderWorldBorder(Entity entityIn, float partialTicks, CallbackInfo ci) {
        if (NO_RENDER.isEnabled() && NO_RENDER.get().worldBorder.getValue()) {
            ci.cancel();
        }
    }

    /*@Inject(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;getEntityLists()[Lnet/minecraft/util/ClassInheritanceMultiMap;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void renderEntitiesHook(Entity i, ICamera flag1, float flag, CallbackInfo ci, int pass, double d0, double d1, double d2, Entity entity, double d3, double d4, double d5, List<Entity> list, Entity entity1, List<Entity> list1, List<Entity> list2, BlockPos.PooledMutableBlockPos blockPos, Object info, Chunk chunk)
    {

    }*/

    @Redirect(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntityStatic(Lnet/minecraft/entity/Entity;FZ)V",
                    ordinal = 1
            )
    )
    public void renderEntityHook(RenderManager instance, Entity entityIn, float partialTicks, boolean p_188388_3_)
    {
        RenderEntityInWorldEvent.Pre pre = new RenderEntityInWorldEvent.Pre(entityIn, partialTicks);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled())
        {
            instance.renderEntityStatic(entityIn, partialTicks, p_188388_3_);
        }
        else
        {
            countEntitiesRendered--; // probably unimportant!
        }
        RenderEntityInWorldEvent.Post post = new RenderEntityInWorldEvent.Post(entityIn);
    }

    @Inject(
            method = "renderSky(FI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    public void renderSkyHook(float f4, int f5, CallbackInfo ci)
    {
        Bus.EVENT_BUS.post(new RenderSkyEvent());
    }

}
