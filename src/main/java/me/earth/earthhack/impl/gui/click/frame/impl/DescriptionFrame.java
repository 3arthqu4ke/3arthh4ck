package me.earth.earthhack.impl.gui.click.frame.impl;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;

public class DescriptionFrame extends Frame
{
    private static final ModuleCache<ClickGui> CLICK_GUI =
            Caches.getModule(ClickGui.class);
    private static final ResourceLocation LEFT_EAR = new ResourceLocation("earthhack:textures/gui/left_ear.png");
    private static final ResourceLocation RIGH_EAR = new ResourceLocation("earthhack:textures/gui/right_ear.png");

    private String description;

    public DescriptionFrame(float posX, float posY, float width, float height)
    {
        super("Description", posX, posY, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (description == null || !CLICK_GUI.get().description.getValue())
        {
            return;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        final Color clr = CLICK_GUI.get().color.getValue();
        if (CLICK_GUI.get().catEars.getValue())
        {
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


        float y = this.getPosY() + 2 + (getHeight() / 2) + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        List<String> strings = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(this.getDescription(), (int) this.getWidth() - 1);

        Render2DUtil.drawRect(getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + 3 + (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1) * strings.size(), 0x92000000);

        for (String string : strings)
        {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, this.getPosX() + 3, y, 0xFFFFFFFF);
            y += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1;
        }
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
