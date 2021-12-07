package me.earth.earthhack.impl.gui.hud.rewrite;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.impl.DescriptionFrame;
import me.earth.earthhack.impl.gui.hud.rewrite.frame.HudElementFrame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class HudEditorGui extends GuiScreen
{

    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final ResourceLocation BLACK_PNG =
            new ResourceLocation("earthhack:textures/gui/black.png");

    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    public static DescriptionFrame descriptionFrame =
            new DescriptionFrame(0, 0, 200, 16);

    public static Map<String, List<SnapPoint>> snapPoints;
    private final Set<HudElement> elements = new HashSet<>();
    private HudElementFrame frame;
    private boolean oldVal = false;
    private boolean attached = false;
    private double mouseClickedX;
    private double mouseClickedY;
    private double mouseReleasedX;
    private double mouseReleasedY;
    private boolean selecting;

    public void init() {
        if (!attached)
        {
            CLICK_GUI.get().descriptionWidth.addObserver(e -> descriptionFrame.setWidth(e.getValue()));
            attached = true;
        }
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        frame = new HudElementFrame();
        descriptionFrame = new DescriptionFrame(resolution.getScaledWidth() - CLICK_GUI.get().descriptionWidth.getValue() - 4, 4, CLICK_GUI.get().descriptionWidth.getValue(), 16);
        frame.init();
        descriptionFrame.init();
        oldVal = CLICK_GUI.get().catEars.getValue();
        snapPoints = new HashMap<>();
        List<SnapPoint> points = new ArrayList<>();
        points.add(new SnapPoint(2,resolution.getScaledHeight() - 4, 2, true, SnapPoint.Orientation.LEFT));
        points.add(new SnapPoint(2, resolution.getScaledHeight() - 4, resolution.getScaledWidth() - 2, true, SnapPoint.Orientation.RIGHT));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, 2, true, SnapPoint.Orientation.TOP));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, resolution.getScaledHeight() - 2, true, SnapPoint.Orientation.BOTTOM));
        points.add(new SnapPoint(2, resolution.getScaledHeight() - 4, resolution.getScaledWidth() / 2.0f, true, SnapPoint.Orientation.VERTICAL_CENTER));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, resolution.getScaledHeight() / 2.0f, true, SnapPoint.Orientation.HORIZONTAL_CENTER));
        snapPoints.put("default", points);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        init();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (mc.world == null)
        {
            if (BACK.getValue())
            {
                this.drawDefaultBackground();
            }
            else
            {
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                this.mc.getTextureManager().bindTexture(BLACK_PNG);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0D, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(this.width, this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, 0).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0).color(64, 64, 64, 255).endVertex();
                tessellator.draw();
            }
        }

        if (oldVal != CLICK_GUI.get().catEars.getValue()) {
            init();
            oldVal = CLICK_GUI.get().catEars.getValue();
        }
        if (CLICK_GUI.get().blur.getValue()) {
            final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            Render2DUtil.drawBlurryRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), CLICK_GUI.get().blurAmount.getValue(),CLICK_GUI.get().blurSize.getValue());
        }

        for (List<SnapPoint> points : snapPoints.values()) {
            for (SnapPoint point : points) {
                if (point.isVisible()) {
                    point.draw(mouseX, mouseY, partialTicks);
                }
                point.update(Managers.ELEMENTS.getRegistered());
            }
        }

        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled()) {
                double minX = Math.min(mouseClickedX, mouseX);
                double minY = Math.min(mouseClickedY, mouseY);
                double maxWidth = Math.max(mouseClickedX, mouseY) - minX;
                double maxHeight = Math.max(mouseClickedY, mouseY) - minY;
                if (GuiUtil.isOverlapping(
                        new double[]{minX, minY, minX + maxWidth, minY + maxHeight},
                        new double[]{element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight()}))
                {
                    elements.add(element);
                }
                element.guiUpdate(mouseX, mouseY, partialTicks);
                element.guiDraw(mouseX, mouseY, partialTicks);
            }
        }

        if (selecting) {
            double minX = Math.min(mouseClickedX, mouseX);
            double minY = Math.min(mouseClickedY, mouseY);
            double maxWidth = Math.max(mouseClickedX, mouseY);
            double maxHeight = Math.max(mouseClickedY, mouseY);
            Render2DUtil.drawRect((float) minX, (float) minY, (float) maxWidth, (float) maxHeight, new Color(255, 255, 255, 128).getRGB());
        }

        frame.drawScreen(mouseX,mouseY,partialTicks);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiKeyPressed(character, keyCode);
            }
        }
        frame.keyTyped(character,keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        List<HudElement> clicked = new ArrayList<>();
        boolean hasDragging = false;
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled() && GuiUtil.isHovered(element, mouseX, mouseY)) {
                clicked.add(element);
                if (element.isDragging()) hasDragging = true;
                // element.guiMouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        clicked.sort(Comparator.comparing(HudElement::getZ));
        if (!clicked.isEmpty()) {
            clicked.get(0).guiMouseClicked(mouseX, mouseY, mouseButton);
        } else {
            if (!GuiUtil.isHovered(frame, mouseX, mouseY) && !hasDragging) {
                selecting = true;
                mouseClickedX = mouseX;
                mouseClickedY = mouseY;
                return;
            }
        }
        frame.mouseClicked(mouseX,mouseY,mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (selecting) {
            mouseReleasedX = mouseX;
            mouseReleasedY = mouseY;
        }
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiMouseReleased(mouseX, mouseY, mouseButton);
                if (elements.remove(element) && selecting) {
                    element.setDraggingX((float) (mouseX - element.getX()));
                    element.setDraggingY((float) (mouseY - element.getY()));
                    element.setDragging(true); // TODO: better solution
                }
            }
        }
        selecting = false;
        frame.mouseReleased(mouseX,mouseY,mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        for (Component comp : frame.getComponents()) {
            if (comp instanceof ModuleComponent) {
                final ModuleComponent moduleComponent = (ModuleComponent) comp;
                for (Component component : moduleComponent.getComponents()) {
                    if (component instanceof KeybindComponent) {
                        final KeybindComponent keybindComponent = (KeybindComponent) component;
                        keybindComponent.setBinding(false);
                    }
                    if (component instanceof StringComponent) {
                        final StringComponent stringComponent = (StringComponent) component;
                        stringComponent.setListening(false);
                    }
                }
            }
            for (HudElement element : Managers.ELEMENTS.getRegistered()) {
                element.setDragging(false);
            }
        }
        selecting = false;
        elements.clear();
    }

    public void onGuiOpened() {
        for (Component comp : frame.getComponents()) {
            if (comp instanceof ModuleComponent) {
                final ModuleComponent moduleComponent = (ModuleComponent) comp;
                for (Component component : moduleComponent.getComponents()) {
                    if (component instanceof ColorComponent) {
                        final ColorComponent colorComponent = (ColorComponent) component;
                        float[] hsb = Color.RGBtoHSB(colorComponent.getColorSetting().getRed(), colorComponent.getColorSetting().getGreen(), colorComponent.getColorSetting().getBlue(), null);
                        colorComponent.setHue(hsb[0]);
                        colorComponent.setSaturation(hsb[1]);
                        colorComponent.setBrightness(hsb[2]);
                        colorComponent.setAlpha(colorComponent.getColorSetting().getAlpha() / 255.f);
                    }
                }
            }
        }
    }

}
