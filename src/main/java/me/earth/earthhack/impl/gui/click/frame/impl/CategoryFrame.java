package me.earth.earthhack.impl.gui.click.frame.impl;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.*;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CategoryFrame extends Frame {
    private final Category moduleCategory;
    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private static final ResourceLocation LEFT_EAR = new ResourceLocation("earthhack:textures/gui/left_ear.png");
    private static final ResourceLocation RIGH_EAR = new ResourceLocation("earthhack:textures/gui/right_ear.png");

    public CategoryFrame(Category moduleCategory, float posX, float posY, float width, float height) {
        super(moduleCategory.name(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
        this.setExtended(true);
    }

    @Override
    public void init() {
        getComponents().clear();
        float offsetY = getHeight() + 1;
        List<Module> moduleList = Managers.MODULES.getModulesFromCategory(getModuleCategory());
        moduleList.sort(Comparator.comparing(Module::getName));
        for (Module module : moduleList) {
            getComponents().add(new ModuleComponent(module, getPosX(), getPosY(), 0, offsetY, getWidth(), 14));
            offsetY += 14;
        }
        super.init();
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final float scrollMaxHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        final Color clr = CLICK_GUI.get().color.getValue();
        if (CLICK_GUI.get().catEars.getValue()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(LEFT_EAR);
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1.0F);
            Gui.drawScaledCustomSizeModalRect((int) getPosX() - 8, (int) getPosY() - 8, 0, 0, 20, 20, 20, 20, 20, 20);
            Minecraft.getMinecraft().getTextureManager().bindTexture(RIGH_EAR);
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1.0F);
            Gui.drawScaledCustomSizeModalRect((int) (getPosX() + getWidth()) - 12, (int) getPosY() - 8, 0, 0, 20, 20, 20, 20, 20, 20);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        Render2DUtil.drawRect(getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), CLICK_GUI.get().color.getValue().getRGB());
        Render2DUtil.drawBorderedRect(getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), 0.5f, 0, 0xff000000);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        if (CLICK_GUI.get().size.getValue()) {
            String disString = "[" + getComponents().size() + "]";
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(disString, (getPosX() + getWidth() - 3 - Minecraft.getMinecraft().fontRenderer.getStringWidth(disString)), (getPosY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1)), 0xFFFFFFFF);
        }
        if (isExtended()) {
            if (RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(), (Math.min(getScrollCurrentHeight(), scrollMaxHeight)) + 1) && getScrollCurrentHeight() > scrollMaxHeight) {
                final float scrollSpeed = Math.min(getScrollCurrentHeight(), scrollMaxHeight) / (Minecraft.getDebugFPS() >> 3);
                int wheel = Mouse.getDWheel();
                if (wheel < 0) {
                    if (getScrollY() - scrollSpeed < -(getScrollCurrentHeight() - Math.min(getScrollCurrentHeight(), scrollMaxHeight)))
                        setScrollY((int) -(getScrollCurrentHeight() - Math.min(getScrollCurrentHeight(), scrollMaxHeight)));
                    else setScrollY((int) (getScrollY() - scrollSpeed));
                } else if (wheel > 0) {
                    setScrollY((int) (getScrollY() + scrollSpeed));
                }
            }
            if (getScrollY() > 0) setScrollY(0);
            if (getScrollCurrentHeight() > scrollMaxHeight) {
                if (getScrollY() - 6 < -(getScrollCurrentHeight() - scrollMaxHeight))
                    setScrollY((int) -(getScrollCurrentHeight() - scrollMaxHeight));
            } else if (getScrollY() < 0) setScrollY(0);
            Render2DUtil.drawRect(getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + 1 + (getCurrentHeight()), 0x92000000);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(getPosX(), getPosY() + getHeight() + 1, getPosX() + getWidth(), getPosY() + getHeight() + scrollMaxHeight + 1);
            getComponents().forEach(component -> component.drawScreen(mouseX, mouseY, partialTicks));
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }
        updatePositions();
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final float scrollMaxHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - getHeight();
        if (isExtended() && RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(), (Math.min(getScrollCurrentHeight(), scrollMaxHeight)) + 1))
            getComponents().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void updatePositions() {
        float offsetY = getHeight() + 1;
        for (Component component : getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(getPosX(), getPosY() + getScrollY());
            if (component instanceof ModuleComponent) {
                if (component.isExtended()) {
                    for (Component component1 : ((ModuleComponent) component).getComponents()) {
                        if (component1 instanceof BooleanComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component1).getBooleanSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                        if (component1 instanceof KeybindComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component1).getBindSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof NumberComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component1).getNumberSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof EnumComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component1).getEnumSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof ColorComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component1).getColorSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                        if (component1 instanceof StringComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component1).getStringSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                        if (component1 instanceof ListComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component1).getListSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                    }
                    offsetY += 3.f;
                }
            }
            offsetY += component.getHeight();
        }
    }

    private float getScrollCurrentHeight() {
        return getCurrentHeight() + getHeight() + 3.f;
    }

    private float getCurrentHeight() {
        float cHeight = 1;
        for (Component component : getComponents()) {
            if (component instanceof ModuleComponent) {
                if (component.isExtended()) {
                    for (Component component1 : ((ModuleComponent) component).getComponents()) {
                        if (component1 instanceof BooleanComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component1).getBooleanSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                        if (component1 instanceof KeybindComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component1).getBindSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof NumberComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component1).getNumberSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof EnumComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component1).getEnumSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof ColorComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component1).getColorSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                        if (component1 instanceof StringComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component1).getStringSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                        if (component1 instanceof ListComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ListComponent) component1).getListSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                    }
                    cHeight += 3.f;
                }
            }
            cHeight += component.getHeight();
        }
        return cHeight;
    }


    public Category getModuleCategory() {
        return moduleCategory;
    }
}
