package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamage;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamagePos;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

final class ListenerRender extends ModuleListener<AutoCrystal, Render3DEvent> {
    private final Map<BlockPos, Long> fadeList = new HashMap<>();
    private static final ResourceLocation CRYSTAL_LOCATION = new ResourceLocation("earthhack:textures/client/crystal.png");

    public ListenerRender(AutoCrystal module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        RenderDamagePos mode = module.renderDamage.getValue();

        if (module.render.getValue()
                && module.box.getValue()
                && module.fade.getValue()
                && !module.isPingBypass()) {
            for (Map.Entry<BlockPos, Long> set : fadeList.entrySet()) {
                if (module.getRenderPos() == set.getKey()) {
                    continue;
                }

                final Color boxColor = module.boxColor.getValue();
                final Color outlineColor = module.outLine.getValue();
                final float maxBoxAlpha = boxColor.getAlpha();
                final float maxOutlineAlpha = outlineColor.getAlpha();
                final float alphaBoxAmount = maxBoxAlpha / module.fadeTime.getValue();
                final float alphaOutlineAmount = maxOutlineAlpha / module.fadeTime.getValue();
                final int fadeBoxAlpha = MathHelper.clamp((int) (alphaBoxAmount * (set.getValue() + module.fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
                final int fadeOutlineAlpha = MathHelper.clamp((int) (alphaOutlineAmount * (set.getValue() + module.fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);

                if (module.box.getValue())
                    RenderUtil.renderBox(
                            Interpolation.interpolatePos(set.getKey(), 1.0f),
                            new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha),
                            new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha),
                            1.5f);

            }
        }

        BlockPos pos;
        if (module.render.getValue()
                && !module.isPingBypass()
                && (pos = module.getRenderPos()) != null) {


            if (!module.fade.getValue()) {

                if (module.box.getValue())
                    RenderUtil.renderBox(
                            Interpolation.interpolatePos(pos, 1.0f),
                            module.boxColor.getValue(),
                            module.outLine.getValue(),
                            1.5f);
            }

            if (mode != RenderDamagePos.None)
                renderDamage(pos);

            if (module.fade.getValue())
                fadeList.put(pos, System.currentTimeMillis());
        }

        fadeList.entrySet().removeIf(e ->
                e.getValue() + module.fadeTime.getValue()
                        < System.currentTimeMillis());

    }

    private void renderDamage(BlockPos pos) {
        String text = module.damage;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        double x = pos.getX() + 0.5;
        double y = pos.getY() + (module.renderDamage.getValue() == RenderDamagePos.OnTop ? 1.35 : 0.5);
        double z = pos.getZ() + 0.5;

        float scale = 0.016666668f * (module.renderMode.getValue() == RenderDamage.Indicator ? 0.95f : 1.3f);

        GlStateManager.translate(x - Interpolation.getRenderPosX(),
                y - Interpolation.getRenderPosY(),
                z - Interpolation.getRenderPosZ());

        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);

        GlStateManager.rotate(mc.player.rotationPitch,
                mc.gameSettings.thirdPersonView == 2
                        ? -1.0f
                        : 1.0f,
                0.0f,
                0.0f);

        GlStateManager.scale(-scale, -scale, scale);

        int distance = (int) mc.player.getDistance(x, y, z);
        float scaleD = (distance / 2.0f) / (2.0f + (2.0f - 1));
        if (scaleD < 1.0f) {
            scaleD = 1;
        }

        GlStateManager.scale(scaleD, scaleD, scaleD);
        TextRenderer m = Managers.TEXT;
        GlStateManager.translate(-(m.getStringWidth(text) / 2.0), 0, 0);
        if (module.renderMode.getValue() == RenderDamage.Indicator) {
            Color clr = module.indicatorColor.getValue();
            Render2DUtil.drawUnfilledCircle(m.getStringWidth(text) / 2.0f, 0, 22.f, new Color(5, 5, 5, clr.getAlpha()).getRGB(), 5.f);
            Render2DUtil.drawCircle(m.getStringWidth(text) / 2.0f, 0, 22.f, clr.getRGB());
            m.drawString(text, 0, 6.0f, new Color(255, 255, 255).getRGB());
            Minecraft.getMinecraft().getTextureManager().bindTexture(CRYSTAL_LOCATION);
            Gui.drawScaledCustomSizeModalRect((int) (m.getStringWidth(text) / 2.0f) - 10, -17, 0, 0, 12, 12, 22, 22, 12, 12);
        } else {
            m.drawStringWithShadow(text, 0, 0, new Color(255, 255, 255).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    }

}

