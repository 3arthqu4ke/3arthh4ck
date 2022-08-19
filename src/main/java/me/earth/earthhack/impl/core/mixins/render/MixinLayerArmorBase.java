package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.RenderArmorEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase {

    private static final ModuleCache<Chams> CHAMS =
            Caches.getModule(Chams.class);
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);

    @Redirect(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderArmorHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderArmorEvent pre = new RenderArmorEvent.Pre(entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled())
        {
            if (CHAMS.get().shouldArmorChams() && CHAMS.isEnabled()) {
                Color color = CHAMS.get().getArmorVisibleColor(entityIn);
                glPushMatrix();
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0F, -2000000F);
                glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }
            pre.getModel().render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (CHAMS.get().shouldArmorChams() && CHAMS.isEnabled()) {
                GL11.glPolygonOffset(1.0F, 2000000F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                glPopMatrix();
            }
        }

        RenderArmorEvent post = new RenderArmorEvent.Post(entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Bus.EVENT_BUS.post(post);
    }

    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (NO_RENDER.returnIfPresent(m -> !m.isValidArmorPiece(slotIn), false)) {
            ci.cancel();
        }
    }

}
