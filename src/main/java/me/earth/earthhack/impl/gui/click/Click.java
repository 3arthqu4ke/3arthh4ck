package me.earth.earthhack.impl.gui.click;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.click.frame.impl.CategoryFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.DescriptionFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.ModulesFrame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.pingbypass.modules.SyncModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Click extends GuiScreen {
    public static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final ResourceLocation BLACK_PNG =
            new ResourceLocation("earthhack:textures/gui/black.png");

    public static DescriptionFrame descriptionFrame =
            new DescriptionFrame(0, 0, 200, 16);

    private final ArrayList<Frame> frames = new ArrayList<>();
    private Category[] categories = Category.values();
    private final ModuleManager moduleManager;
    private boolean oldVal = false;
    private boolean attached = false;
    private boolean addDescriptionFrame = true;
    private boolean pingBypass;

    public final GuiScreen screen;

    public Click(GuiScreen screen) {
        this.moduleManager = Managers.MODULES;
        this.screen = screen;
    }

    public Click(GuiScreen screen, ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.screen = screen;
    }

    public void init() {
        if (!attached)
        {
            CLICK_GUI.get().descriptionWidth.addObserver(e -> descriptionFrame.setWidth(e.getValue()));
            attached = true;
        }

        getFrames().clear();
        int x = CLICK_GUI.get().catEars.getValue() ? 14 : 2;
        int y = CLICK_GUI.get().catEars.getValue() ? 14 : 2;
        for (Category moduleCategory : categories) {
            if (moduleManager.getModulesFromCategory(moduleCategory).size() > 0) {
                getFrames().add(new CategoryFrame(moduleCategory, moduleManager, x, y, 110, 16));
                if (x + 220 >= new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()) {
                    x = CLICK_GUI.get().catEars.getValue() ? 14 : 2;
                    y += CLICK_GUI.get().catEars.getValue() ? 32 : 20;
                } else x += (CLICK_GUI.get().catEars.getValue() ? 132 : 112);
            }
        }

        if (addDescriptionFrame) {
            descriptionFrame = new DescriptionFrame(x, y, CLICK_GUI.get().descriptionWidth.getValue(), 16);
            getFrames().add(descriptionFrame);
        }

        if (pingBypass) {
            DescriptionFrame hint = new DescriptionFrame("Info", x, y + 100, CLICK_GUI.get().descriptionWidth.getValue(), 16);
            hint.setDescription("You are editing the modules running on the PingBypass server, not the ones which run here on your client.");
            getFrames().add(hint);

            ModulesFrame pbFrame = new ModulesFrame("PingBypass", x, y + 200, 110, 16);
            pbFrame.getComponents().add(new ModuleComponent(new SyncModule(), pbFrame.getPosX(), pbFrame.getPosY(), 0, pbFrame.getHeight() + 1, pbFrame.getWidth(), 14));
            getFrames().add(pbFrame);
        }

        getFrames().forEach(Frame::init);
        oldVal = CLICK_GUI.get().catEars.getValue();
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

        getFrames().forEach(frame -> frame.drawScreen(mouseX,mouseY,partialTicks));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        getFrames().forEach(frame -> frame.keyTyped(character,keyCode));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseClicked(mouseX,mouseY,mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseReleased(mouseX,mouseY,mouseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        getFrames().forEach(frame -> {
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
            }
        });
    }

    public void onGuiOpened() {
        getFrames().forEach(frame -> {
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
        });
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setPingBypass(boolean pingBypass) {
        this.pingBypass = pingBypass;
    }

    public void setAddDescriptionFrame(boolean addDescriptionFrame) {
        this.addDescriptionFrame = addDescriptionFrame;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

}
