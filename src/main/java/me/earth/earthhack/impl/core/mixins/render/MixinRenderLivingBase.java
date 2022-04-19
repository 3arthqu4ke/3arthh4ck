package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderLayersEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// TODO: why is there two renderlivingbase mixins?
@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase
{

    @Shadow protected List<LayerRenderer<?>> layerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "renderLayers", at = @At("HEAD"), cancellable = true)
    public void renderLayersPreHook(EntityLivingBase entitylivingbaseIn,
                                    float limbSwing,
                                    float limbSwingAmount,
                                    float partialTicks,
                                    float ageInTicks,
                                    float netHeadYaw,
                                    float headPitch,
                                    float scaleIn,
                                    CallbackInfo ci)
    {
        RenderLayersEvent pre = new RenderLayersEvent(Render.class.cast(this), entitylivingbaseIn, layerRenderers, Stage.PRE);
        Bus.EVENT_BUS.post(pre);
        if (pre.isCancelled())
        {
            ci.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "renderLayers", at = @At("RETURN"))
    public void renderLayersPostHook(EntityLivingBase entitylivingbaseIn,
                                    float limbSwing,
                                    float limbSwingAmount,
                                    float partialTicks,
                                    float ageInTicks,
                                    float netHeadYaw,
                                    float headPitch,
                                    float scaleIn,
                                    CallbackInfo ci)
    {
        RenderLayersEvent post = new RenderLayersEvent(Render.class.cast(this), entitylivingbaseIn, layerRenderers, Stage.POST);
        Bus.EVENT_BUS.post(post);
    }

}
