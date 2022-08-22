package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderItemInFirstPersonEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.handchams.HandChams;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer
{
    @Shadow protected abstract void
    renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_);

    @Shadow public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    private static final ResourceLocation RESOURCE = new ResourceLocation("textures/rainbow.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static final ModuleCache<ViewModel> VIEW_MODEL =
            Caches.getModule(ViewModel.class);
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);
    private static final ModuleCache<HandChams> HAND_CHAMS =
            Caches.getModule(HandChams.class);

    @Inject(
        method = "renderFireInFirstPerson",
        at = @At("HEAD"),
        cancellable = true)
    public void renderFireInFirstPersonHook(CallbackInfo info)
    {
        if (NO_RENDER.returnIfPresent(NoRender::noFire, false))
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "renderItemInFirstPerson(F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;" +
                    "renderItemInFirstPerson(" +
                    "Lnet/minecraft/client/entity/AbstractClientPlayer;" +
                    "FFLnet/minecraft/util/EnumHand;" +
                    "FLnet/minecraft/item/ItemStack;" +
                    "F)V"))
    public void renderItemInFirstPersonHook(ItemRenderer itemRenderer,
                                             AbstractClientPlayer player,
                                             float drinkOffset,
                                             float mapAngle,
                                             EnumHand hand,
                                             float x,
                                             ItemStack stack,
                                             float y)
    {
        float xOffset = VIEW_MODEL.isPresent()
                            ? VIEW_MODEL.get().getX(hand)
                            : 0;

        float yOffset = VIEW_MODEL.isPresent()
                            ? VIEW_MODEL.get().getY(hand)
                            : 0;

        itemRenderer.renderItemInFirstPerson(player,
                                             drinkOffset,
                                             mapAngle,
                                             hand,
                                             x + xOffset,
                                             stack,
                                             y + yOffset);

    }

    @Inject(
        method = "renderItemInFirstPerson(" +
                "Lnet/minecraft/client/entity/AbstractClientPlayer;" +
                "FFLnet/minecraft/util/EnumHand;" +
                "FLnet/minecraft/item/ItemStack;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;" +
                     "pushMatrix()V",
            shift = At.Shift.AFTER))
    public void pushMatrixHook(CallbackInfo info)
    {
        if (VIEW_MODEL.isEnabled())
        {
            float[] scale = VIEW_MODEL.isPresent()
                    ? VIEW_MODEL.get().getScale()
                    : ViewModel.DEFAULT_SCALE;

            float[] translation = VIEW_MODEL.isPresent()
                    ? VIEW_MODEL.get().getTranslation()
                    : ViewModel.DEFAULT_TRANSLATION;

            GL11.glScalef(scale[0], scale[1], scale[2]);
            GL11.glRotatef(translation[0],
                           translation[1],
                           translation[2],
                           translation[3]);

            // ???????????????????? this fucks nametags
            // GlStateManager.enableDepth();
        }
    }

    /*@Redirect(
            method = "renderItemSide",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V")
    )
    public void mrsHook(RenderItem renderItem, ItemStack stack, EntityLivingBase entitylivingbaseIn, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        RenderItemInFirstPersonEvent pre = new RenderItemInFirstPersonEvent(renderItem, stack, entitylivingbaseIn, transform, leftHanded, Stage.PRE);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled())
        {
            renderItem.renderItem(stack, entitylivingbaseIn, transform, leftHanded);
        }
        RenderItemInFirstPersonEvent post = new RenderItemInFirstPersonEvent(renderItem, stack, entitylivingbaseIn, transform, leftHanded, Stage.POST);
        Bus.EVENT_BUS.post(post);
    }*/

    @Redirect(
            method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V")
    )
    public void captainHook(ItemRenderer itemRenderer, EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        RenderItemInFirstPersonEvent pre = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, Stage.PRE);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled())
        {
            itemRenderer.renderItemSide(entitylivingbaseIn, pre.getStack(), pre.getTransformType(), leftHanded);
        }
        RenderItemInFirstPersonEvent post = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, Stage.POST);
        Bus.EVENT_BUS.post(post);
    }

    @Redirect(
            method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderArmFirstPerson(FFLnet/minecraft/util/EnumHandSide;)V"))
    public void mrHook(ItemRenderer itemRenderer, float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        if (HAND_CHAMS.isEnabled()) {
            if (HAND_CHAMS.get().mode.getValue() == ChamsMode.Normal) {
                if (HAND_CHAMS.get().chams.getValue()) {
                    Color handColor = HAND_CHAMS.get().color.getValue();
                    glPushMatrix();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glEnable(GL_BLEND);
                    glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    glColor4f(handColor.getRed() / 255.0f, handColor.getGreen() / 255.0f, handColor.getBlue() / 255.0f, handColor.getAlpha() / 255.0f);
                    renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                    glDisable(GL_BLEND);
                    glEnable(GL_TEXTURE_2D);
                    glPopAttrib();
                    glPopMatrix();
                }

                if (HAND_CHAMS.get().wireframe.getValue()) {
                    Color handColor = HAND_CHAMS.get().wireFrameColor.getValue();
                    glPushMatrix();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glEnable(GL_BLEND);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    glLineWidth(1.5f);
                    glDisable(GL_DEPTH_TEST);
                    glDepthMask(false);
                    glColor4f(handColor.getRed() / 255.0f, handColor.getGreen() / 255.0f, handColor.getBlue() / 255.0f, handColor.getAlpha() / 255.0f);
                    renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                    glDisable(GL_BLEND);
                    glEnable(GL_TEXTURE_2D);
                    glPopAttrib();
                    glPopMatrix();
                }
            } else {
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glDisable(GL_ALPHA_TEST);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                glDisable(GL_TEXTURE_2D);
                glEnable(GL_POLYGON_OFFSET_LINE);
                glEnable(GL_STENCIL_TEST);
                renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                glEnable(GL_TEXTURE_2D);
                renderEffect(p_187456_1_, p_187456_2_, p_187456_3_);
                glPopAttrib();
            }
        } else {
            renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
        }
    }

    @Inject(
            method = "rotateArm",
            at = @At("HEAD"),
            cancellable = true)
    public void rotateArmHook(float p_187458_1_, CallbackInfo ci) {
        if (VIEW_MODEL.isEnabled() && VIEW_MODEL.get().noSway.getValue()) {
            ci.cancel();
        }
    }

    private void renderEffect(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        float f = (float)Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(0.5F, 0.5F, 0.5F, 0.5f);

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.disableLighting();
            // GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            float f2 = 0.76F;
            GlStateManager.color(0.38F, 0.19F, 0.608F, 0.5f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334F;
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            // renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
            RenderPlayer renderPlayer = (RenderPlayer)Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(Minecraft.getMinecraft().player);
            if (renderPlayer != null) {
                if (p_187456_3_ == EnumHandSide.RIGHT) {
                    renderPlayer.renderRightArm(Minecraft.getMinecraft().player);
                } else {
                    renderPlayer.renderLeftArm(Minecraft.getMinecraft().player);
                }
            }
            // GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    /*public void renderRightArm(AbstractClientPlayer clientPlayer, RenderPlayer renderPlayer)
    {
        float f = 1.0F;
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        float f1 = 0.0625F;
        ModelPlayer modelplayer = renderPlayer.getMainModel();
        ((IRenderPlayer)renderPlayer).setModelVisibilities(clientPlayer);
        GlStateManager.enableBlend();
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedRightArm.rotateAngleX = 0.0F;
        modelplayer.bipedRightArm.render(0.0625F);
        modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
        modelplayer.bipedRightArmwear.render(0.0625F);
        GlStateManager.disableBlend();
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer, RenderPlayer renderPlayer)
    {
        float f = 1.0F;
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        float f1 = 0.0625F;
        ModelPlayer modelplayer = renderPlayer.getMainModel();
        ((IRenderPlayer)renderPlayer).setModelVisibilities(clientPlayer);
        GlStateManager.enableBlend();
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
        modelplayer.bipedLeftArm.render(0.0625F);
        modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
        modelplayer.bipedLeftArmwear.render(0.0625F);
        GlStateManager.disableBlend();
    }*/

}
