package me.earth.earthhack.impl.modules.render.crystalchams;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class CrystalChams extends Module {
    public final Setting<ChamsMode> mode       =
        register(new EnumSetting<>("Mode", ChamsMode.Normal));
    public final Setting<Boolean> chams        =
        register(new BooleanSetting("Chams", false));
    public final Setting<Boolean> throughWalls =
        register(new BooleanSetting("ThroughWalls", false));
    public final Setting<Boolean> wireframe    =
        register(new BooleanSetting("Wireframe", false));
    public final Setting<Boolean> wireWalls    =
        register(new BooleanSetting("WireThroughWalls", false));
    public final Setting<Boolean> texture    =
        register(new BooleanSetting("Texture", false));
    public final NumberSetting<Float> lineWidth =
        register(new NumberSetting<>("LineWidth" , 1f , 0.1f , 4f));
    public final Setting<Color> color          =
        register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> wireFrameColor =
        register(new ColorSetting("WireframeColor", new Color(255, 255, 255, 255)));

    public CrystalChams() {
        super("CrystalChams", Category.Render);
        this.listeners.add(new LambdaListener<>(CrystalRenderEvent.Pre.class, e -> {
            if (!texture.getValue()) {
                e.setCancelled(true);
            }

            if (mode.getValue() == ChamsMode.Gradient) {
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glEnable(GL_BLEND);
                glDisable(GL_LIGHTING);
                glDisable(GL_TEXTURE_2D);
                float alpha = color.getValue().getAlpha() / 255.0f;
                glColor4f(1.0f, 1.0f, 1.0f, alpha);
                e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(),
                                    e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                glEnable(GL_TEXTURE_2D);

                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                float f = (float) e.getEntity().ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/rainbow.png"));
                Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                GlStateManager.enableBlend();
                GlStateManager.depthFunc(514);
                GlStateManager.depthMask(false);
                GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

                for (int i = 0; i < 2; ++i)
                {
                    GlStateManager.disableLighting();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                    GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 0.5F);
                    GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                    GlStateManager.matrixMode(5888);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                }

                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(515);
                GlStateManager.disableBlend();
                mc.entityRenderer.setupFogColor(false);
                glPopAttrib();
            } else {
                if (wireframe.getValue()) {
                    Color wireColor = wireFrameColor.getValue();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glLineWidth(lineWidth.getValue());
                    if (wireWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }

                    glColor4f(wireColor.getRed() / 255.0f,
                              wireColor.getGreen() / 255.0f,
                              wireColor.getBlue() / 255.0f,
                              wireColor.getAlpha() / 255.0f);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(),
                                        e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                    glPopAttrib();
                }

                if (chams.getValue()) {
                    Color chamsColor = color.getValue();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glDisable(GL_ALPHA_TEST);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glEnable(GL_STENCIL_TEST);
                    glEnable(GL_POLYGON_OFFSET_LINE);
                    if (throughWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }
                    glColor4f(chamsColor.getRed() / 255.0f,
                              chamsColor.getGreen() / 255.0f,
                              chamsColor.getBlue() / 255.0f,
                              chamsColor.getAlpha() / 255.0f);
                    e.getModel().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(),
                                        e.getAgeInTicks(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScale());
                    glPopAttrib();
                }
            }
        }));
    }

}
