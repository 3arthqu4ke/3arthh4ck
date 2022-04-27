package me.earth.hudplugin;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.hudplugin.mixin.IRenderGlobal;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import scala.Int;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.earth.hudplugin.HUDModule.drawLine;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glVertex3d;

public class ComeponentModule extends Module
{
    public static final ComeponentModule INSTANCE = new ComeponentModule();

    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");

    private static final double HALF_PI = Math.PI / 2;

    private Map<EntityPlayer, Map<Integer, ItemStack>> hotbarMap = new HashMap<>();

    public Setting<Boolean> inventory = register(new BooleanSetting("Inventory", false));
    public Setting<Integer> invX = register(new NumberSetting<>("InvX", 564, 0, 1000));
    public Setting<Integer> invY = register(new NumberSetting<>("InvY", 467, 0, 1000));
    public Setting<Integer> fineinvX = register(new NumberSetting<>("InvFineX", 0));
    public Setting<Integer> fineinvY = register(new NumberSetting<>("InvFineY", 0));
    public Setting<Boolean> renderXCarry = register(new BooleanSetting("RenderXCarry", false));
    public Setting<Integer> invH = register(new NumberSetting<>("InvH", 3));
    public Setting<Boolean> holeHud = register(new BooleanSetting("HoleHUD", false));
    public Setting<Integer> holeX = register(new NumberSetting<>("HoleX", 279, 0, 1000));
    public Setting<Integer> holeY = register(new NumberSetting<>("HoleY", 485, 0, 1000));
    public Setting<Compass> compass = register(new EnumSetting<>("Compass", Compass.NONE));
    public Setting<Integer> compassX = register(new NumberSetting<>("CompX", 472, 0, 1000));
    public Setting<Integer> compassY = register(new NumberSetting<>("CompY", 424, 0, 1000));
    public Setting<Integer> scale = register(new NumberSetting<>("Scale", 3, 0, 10));
    public Setting<Boolean> playerViewer = register(new BooleanSetting("PlayerViewer", false));
    public Setting<Integer> playerViewerX = register(new NumberSetting<>("PlayerX", 752, 0, 1000));
    public Setting<Integer> playerViewerY = register(new NumberSetting<>("PlayerY", 497, 0, 1000));
    public Setting<Float> playerScale = register(new NumberSetting<>("PlayerScale", 1.0f, 0.1f, 2.0f));
    public Setting<Boolean> clock = register(new BooleanSetting("Clock", true));
    public Setting<Boolean> clockFill = register(new BooleanSetting("ClockFill", true));
    public Setting<Float> clockX = register(new NumberSetting<>("ClockX", 2.0f, 0.0f, 1000.0f));
    public Setting<Float> clockY = register(new NumberSetting<>("ClockY", 2.0f, 0.0f, 1000.0f));
    public Setting<Float> clockRadius = register(new NumberSetting<>("ClockRadius", 6.0f, 0.0f, 100.0f));
    public Setting<Float> clockLineWidth = register(new NumberSetting<>("ClockLineWidth", 1.0f, 0.0f, 5.0f));
    public Setting<Integer> clockSlices = register(new NumberSetting<>("ClockSlices", 360, 1, 720));
    public Setting<Integer> clockLoops = register(new NumberSetting<>("ClockLoops", 1, 1, 720));

    private ComeponentModule() {
        super("Phobos-Components", Category.Client);
        this.listeners.add(new EventListener<Render2DEvent>(Render2DEvent.class)
        {
            @Override
            public void invoke(Render2DEvent event)
            {
                onRender2D(event);
            }
        });
    }

    public void onRender2D(Render2DEvent event) {
        if(playerViewer.getValue()) {
            drawPlayer();
        }

        if(compass.getValue() != Compass.NONE) {
            drawCompass();
        }

        if (holeHud.getValue()) {
            drawOverlay(((IMinecraft)mc).getTimer().renderPartialTicks);
        }

        if(inventory.getValue()) {
            renderInventory();
        }

        if (clock.getValue())
        {
            drawClock(clockX.getValue(), clockY.getValue(), clockRadius.getValue(), clockSlices.getValue(), clockLoops.getValue(), clockLineWidth.getValue(), clockFill.getValue(), new Color(255, 0, 0, 255));
        }
    }

    public static void drawClock(float x, float y, float radius, int slices, int loops, float lineWidth, boolean fill, Color color) {
        Disk disk = new Disk();
        Date date = new Date();
        int hourAngle = 180 + -((Calendar.getInstance().get(Calendar.HOUR) * 30) + (Calendar.getInstance().get(Calendar.MINUTE) / 2));
        int minuteAngle = 180 + -((Calendar.getInstance().get(Calendar.MINUTE) * 6) + (Calendar.getInstance().get(Calendar.SECOND) / 10));
        int secondAngle = 180 + -((Calendar.getInstance().get(Calendar.SECOND) * 6));
        int totalMinutesTime = Calendar.getInstance().get(Calendar.MINUTE);
        int totalHoursTime = Calendar.getInstance().get(Calendar.HOUR);
        if (fill) {
            GL11.glPushMatrix();
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glLineWidth(lineWidth);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            disk.setOrientation(GLU.GLU_OUTSIDE);
            disk.setDrawStyle(GLU.GLU_FILL);
            GL11.glTranslated(x, y, 0.0d);
            disk.draw(0.0f, radius, slices, loops);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glLineWidth(lineWidth);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            List<Vec2f> hVectors = new ArrayList<>();
            float hue = (System.currentTimeMillis() % (360 * 20)) / (360f * 20);;
            for (int i = 0; i <= 360; i++) {
                Vec2f vec = new Vec2f(x + (float) Math.sin(i * Math.PI / 180.0) * radius, y + (float) Math.cos(i * Math.PI / 180.0) * radius);
                hVectors.add(vec);
            }
            Color color1 = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
            for (int j = 0; j < hVectors.size() - 1; j++) {
                glColor4f(color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, color1.getAlpha() / 255f);
                glVertex3d(hVectors.get(j).x, hVectors.get(j).y, 0.0d);
                glVertex3d(hVectors.get(j + 1).x, hVectors.get(j + 1).y, 0.0d);
                hue += (1.0f / 360.0f);
                color1 = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
            }
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
        drawLine(x, y, x + ((float) Math.sin(hourAngle * Math.PI / 180.0) * (radius / 2.0f)), y + ((float) Math.cos(hourAngle * Math.PI / 180.0) * (radius / 2.0f)), 1.0f, Color.WHITE.getRGB());
        drawLine(x, y, x + ((float) Math.sin(minuteAngle * Math.PI / 180.0) * (radius - (radius / 10.0f))), y + ((float) Math.cos(minuteAngle * Math.PI / 180.0) * (radius - (radius / 10.0f))), 1.0f, Color.WHITE.getRGB());
        drawLine(x, y, x + ((float) Math.sin(secondAngle * Math.PI / 180.0) * (radius - (radius / 10.0f))), y + ((float) Math.cos(secondAngle * Math.PI / 180.0) * (radius - (radius / 10.0f))), 1.0f, Color.RED.getRGB());
    }

    public static float wrap(float valI) {
        float val = valI % 360.0f;
        if (val >= 180.0f)
            val -= 360.0f;
        if (val < -180.0f)
            val += 360.0f;
        return val;
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void glScissor(float x, float y, float x1, float y1, final ScaledResolution sr) {
        GL11.glScissor((int) (x * sr.getScaleFactor()), (int) (mc.displayHeight - (y1 * sr.getScaleFactor())), (int) ((x1 - x) * sr.getScaleFactor()), (int) ((y1 - y) * sr.getScaleFactor()));
    }

    public void drawCompass() {
        final ScaledResolution sr = new ScaledResolution(mc);
        if(compass.getValue() == Compass.LINE) {
            float playerYaw = mc.player.rotationYaw;
            float rotationYaw = wrap(playerYaw);
            drawRect(compassX.getValue(), compassY.getValue(), compassX.getValue() + 100, compassY.getValue() + Managers.TEXT.getStringHeight(), 0x75101010);
            glScissor(compassX.getValue(), compassY.getValue(), compassX.getValue() + 100, compassY.getValue() + Managers.TEXT.getStringHeight(), sr);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            final float zeroZeroYaw = wrap((float) (Math.atan2(0 - mc.player.posZ, 0 - mc.player.posX) * 180.0d / Math.PI) - 90.0f);
            HUDModule.drawLine(compassX.getValue() - rotationYaw + (100 / 2) + zeroZeroYaw, compassY.getValue() + 2, compassX.getValue() - rotationYaw + (100 / 2) + zeroZeroYaw, compassY.getValue() + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFF1010);
            HUDModule.drawLine((compassX.getValue() - rotationYaw + (100 / 2)) + 45, compassY.getValue() + 2, (compassX.getValue() - rotationYaw + (100 / 2)) + 45, compassY.getValue() + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
            HUDModule.drawLine((compassX.getValue() - rotationYaw + (100 / 2)) - 45, compassY.getValue() + 2, (compassX.getValue() - rotationYaw + (100 / 2)) - 45, compassY.getValue() + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
            HUDModule.drawLine((compassX.getValue() - rotationYaw + (100 / 2)) + 135, compassY.getValue() + 2, (compassX.getValue() - rotationYaw + (100 / 2)) + 135, compassY.getValue() + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
            HUDModule.drawLine((compassX.getValue() - rotationYaw + (100 / 2)) - 135, compassY.getValue() + 2, (compassX.getValue() - rotationYaw + (100 / 2)) - 135, compassY.getValue() + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
            Managers.TEXT.drawStringWithShadow("n", (compassX.getValue() - rotationYaw + (100 / 2)) + 180 - Managers.TEXT.getStringWidth("n") / 2.0f, compassY.getValue(), 0xFFFFFFFF);
            Managers.TEXT.drawStringWithShadow("n", (compassX.getValue() - rotationYaw + (100 / 2)) - 180 - Managers.TEXT.getStringWidth("n") / 2.0f, compassY.getValue(), 0xFFFFFFFF);
            Managers.TEXT.drawStringWithShadow("e", (compassX.getValue() - rotationYaw + (100 / 2)) - 90 - Managers.TEXT.getStringWidth("e") / 2.0f, compassY.getValue(), 0xFFFFFFFF);
            Managers.TEXT.drawStringWithShadow("s", (compassX.getValue() - rotationYaw + (100 / 2)) - Managers.TEXT.getStringWidth("s") / 2.0f, compassY.getValue(), 0xFFFFFFFF);
            Managers.TEXT.drawStringWithShadow("w", (compassX.getValue() - rotationYaw + (100 / 2)) + 90 - Managers.TEXT.getStringWidth("w") / 2.0f, compassY.getValue(), 0xFFFFFFFF);
            drawLine((compassX.getValue() + 100 / 2), compassY.getValue() + 1, (compassX.getValue() + 100 / 2), compassY.getValue() + Managers.TEXT.getStringHeight() - 1, 2, 0xFF909090);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            final double centerX = compassX.getValue();
            final double centerY = compassY.getValue();
            for (Direction dir : Direction.values()) {
                double rad = getPosOnCompass(dir);
                Managers.TEXT.drawStringWithShadow(dir.name(), (float) (centerX + getX(rad)), (float) (centerY + getY(rad)), dir == Direction.N ? 0xFFFF0000 : 0xFFFFFFFF);
            }
        }
    }

    public void drawPlayer(EntityPlayer player, int x, int y) {
        final EntityPlayer ent = player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(playerViewerX.getValue() + 25, playerViewerY.getValue() + 25, 50.0f);
        GlStateManager.scale(-50.0f * playerScale.getValue(), 50.0f * playerScale.getValue(), 50.0f * playerScale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(playerViewerY.getValue() / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        } catch(Exception ignored) {}
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    public void drawPlayer() {
        final EntityPlayer ent = mc.player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(playerViewerX.getValue() + 25, playerViewerY.getValue() + 25, 50.0f);
        GlStateManager.scale(-50.0f * playerScale.getValue(), 50.0f * playerScale.getValue(), 50.0f * playerScale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(playerViewerY.getValue() / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        } catch(Exception ignored) {}
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    private double getX(double rad) {
        return Math.sin(rad) * (scale.getValue() * 10);
    }

    private double getY(double rad) {
        final double epicPitch = MathHelper.clamp(mc.player.rotationPitch + 30f, -90f, 90f);
        final double pitchRadians = Math.toRadians(epicPitch); // player pitch
        return Math.cos(rad) * Math.sin(pitchRadians) * (scale.getValue() * 10);
    }

    private enum Direction {
        N,
        W,
        S,
        E
    }

    private static double getPosOnCompass(Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.player.rotationYaw));
        int index = dir.ordinal();
        return yaw + (index * HALF_PI);
    }

    public enum Compass {
        NONE,
        CIRCLE,
        LINE
    }

    public void drawOverlay(float partialTicks) {
        float yaw = 0;
        final int dir = (MathHelper.floor((double) (mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);

        switch (dir) {
            case 1:
                yaw = 90;
                break;
            case 2:
                yaw = -180;
                break;
            case 3:
                yaw = -90;
                break;
            default:
        }

        final BlockPos northPos = this.traceToBlock(partialTicks, yaw);
        final Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            final int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                drawRect(holeX.getValue() + 16, holeY.getValue(), holeX.getValue() + 32, holeY.getValue() + 16, 0x60ff0000);
            }
            this.drawBlock(north, holeX.getValue() + 16, holeY.getValue());
        }

        final BlockPos southPos = this.traceToBlock(partialTicks, yaw - 180.0f);
        final Block south = this.getBlock(southPos);
        if (south != null && south != Blocks.AIR) {
            final int damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                drawRect(holeX.getValue() + 16, holeY.getValue() + 32, holeX.getValue() + 32, holeY.getValue() + 48, 0x60ff0000);
            }
            this.drawBlock(south, holeX.getValue() + 16, holeY.getValue() + 32);
        }

        final BlockPos eastPos = this.traceToBlock(partialTicks, yaw + 90.0f);
        final Block east = this.getBlock(eastPos);
        if (east != null && east != Blocks.AIR) {
            final int damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                drawRect(holeX.getValue() + 32, holeY.getValue() + 16, holeX.getValue() + 48, holeY.getValue() + 32, 0x60ff0000);
            }
            this.drawBlock(east, holeX.getValue() + 32, holeY.getValue() + 16);
        }

        final BlockPos westPos = this.traceToBlock(partialTicks, yaw - 90.0f);
        final Block west = this.getBlock(westPos);
        if (west != null && west != Blocks.AIR) {
            final int damage = this.getBlockDamage(westPos);

            if (damage != 0) {
                drawRect(holeX.getValue(), holeY.getValue() + 16, holeX.getValue() + 16, holeY.getValue() + 32, 0x60ff0000);
            }
            this.drawBlock(west, holeX.getValue(), holeY.getValue() + 16);
        }
    }

    public void drawOverlay(float partialTicks, Entity player, int x, int y) {
        float yaw = 0;
        final int dir = (MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);

        switch (dir) {
            case 1:
                yaw = 90;
                break;
            case 2:
                yaw = -180;
                break;
            case 3:
                yaw = -90;
                break;
            default:
        }

        final BlockPos northPos = this.traceToBlock(partialTicks, yaw, player);
        final Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            final int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                drawRect(x + 16, y, x + 32, y + 16, 0x60ff0000);
            }
            this.drawBlock(north, x + 16, y);
        }

        final BlockPos southPos = this.traceToBlock(partialTicks, yaw - 180.0f, player);
        final Block south = this.getBlock(southPos);
        if (south != null && south != Blocks.AIR) {
            final int damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                drawRect(x + 16, y + 32, x + 32, y + 48, 0x60ff0000);
            }
            this.drawBlock(south, x + 16, y + 32);
        }

        final BlockPos eastPos = this.traceToBlock(partialTicks, yaw + 90.0f, player);
        final Block east = this.getBlock(eastPos);
        if (east != null && east != Blocks.AIR) {
            final int damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                drawRect(x + 32, y + 16, x + 48, y + 32, 0x60ff0000);
            }
            this.drawBlock(east, x + 32, y + 16);
        }

        final BlockPos westPos = this.traceToBlock(partialTicks, yaw - 90.0f, player);
        final Block west = this.getBlock(westPos);
        if (west != null && west != Blocks.AIR) {
            final int damage = this.getBlockDamage(westPos);

            if (damage != 0) {
                drawRect(x, y + 16, x + 16, y + 32, 0x60ff0000);
            }
            this.drawBlock(west, x, y + 16);
        }
    }

    private int getBlockDamage(BlockPos pos) {
        for (DestroyBlockProgress destBlockProgress : ((IRenderGlobal) mc.renderGlobal).getDamagedBlocks().values()) {
            if (destBlockProgress.getPosition().getX() == pos.getX() && destBlockProgress.getPosition().getY() == pos.getY() && destBlockProgress.getPosition().getZ() == pos.getZ()) {
                return destBlockProgress.getPartialBlockDamage();
            }
        }
        return 0;
    }

    private BlockPos traceToBlock(float partialTicks, float yaw) {
        final Vec3d pos = Interpolation.interpolateEntity(mc.player);
        final Vec3d dir = direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    public static Vec3d direction(float yaw) {
        return new Vec3d(Math.cos(degToRad(yaw + 90f)), 0, Math.sin(degToRad(yaw + 90f)));
    }

    public static double degToRad(double deg) {
        return deg * (float) (Math.PI / 180.0f);
    }

    private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
        final Vec3d pos = Interpolation.interpolateEntity(player);
        final Vec3d dir = direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private Block getBlock(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        if ((block == Blocks.BEDROCK) || (block == Blocks.OBSIDIAN)) {
            return block;
        }
        return Blocks.AIR;
    }

    private void drawBlock(Block block, float x, float y) {
        final ItemStack stack = new ItemStack(block);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate(x, y, 0);
        mc.getRenderItem().zLevel = 501;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getRenderItem().zLevel = 0.f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    public void renderInventory() {
        this.boxrender(invX.getValue() + fineinvX.getValue(), invY.getValue() + fineinvY.getValue());
        this.itemrender(mc.player.inventory.mainInventory, invX.getValue() + fineinvX.getValue(), invY.getValue() + fineinvY.getValue());
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();
        GlStateManager.color(255, 255, 255, 255);
    }

    private static void postboxrender() {
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    private void boxrender(final int x, final int y) {
        preboxrender();
        mc.renderEngine.bindTexture(box);
        drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        drawTexturedRect(x, y + 16, 0, 16, 176, 54 + invH.getValue(), 500);
        drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        postboxrender();
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = tessellator.getBuffer();
        BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder.pos(x + 0, y + height, zLevel).tex((float) (textureX + 0) * 0.00390625F, (float) (textureY + height) * 0.00390625F).endVertex();
        BufferBuilder.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625F, (float) (textureY + height) * 0.00390625F).endVertex();
        BufferBuilder.pos(x + width, y + 0, zLevel).tex((float) (textureX + width) * 0.00390625F, (float) (textureY + 0) * 0.00390625F).endVertex();
        BufferBuilder.pos(x + 0, y + 0, zLevel).tex((float) (textureX + 0) * 0.00390625F, (float) (textureY + 0) * 0.00390625F).endVertex();
        tessellator.draw();
    }

    private void itemrender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int i = 0; i < items.size() - 9; i++) {
            int iX = x + (i % 9) * (18) + 8;
            int iY = y + (i / 9) * (18) + 18;
            ItemStack itemStack = items.get(i + 9);
            preitemrender();
            mc.getRenderItem().zLevel = 501;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, iY, null);
            mc.getRenderItem().zLevel = 0.f;
            postitemrender();
        }

        if(renderXCarry.getValue()) {
            for (int i = 1; i < 5; i++) {
                int iX = x + ((i + 4) % 9) * (18) + 8;
                ItemStack itemStack = mc.player.inventoryContainer.inventorySlots.get(i).getStack();
                if(itemStack != null && !itemStack.isEmpty()) {
                    preitemrender();
                    mc.getRenderItem().zLevel = 501;
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, y + 1);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, y + 1, null);
                    mc.getRenderItem().zLevel = 0.f;
                    postitemrender();
                }
            }
        }
        /*
        for(int size = items.size(), item = 9; item < size; ++item) {
            final int slotx = x + 1 + item % 9 * 18;
            final int sloty = y + 1 + (item / 9 - 1) * 18;
            preitemrender();
            mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), slotx, sloty);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, items.get(item), slotx, sloty);
            postitemrender();
        }*/
    }

    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(width, height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(width, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public enum TargetHudDesign {
        NORMAL,
        COMPACT
    }

}

