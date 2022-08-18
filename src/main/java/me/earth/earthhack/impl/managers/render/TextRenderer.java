package me.earth.earthhack.impl.managers.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.font.CustomFontRenderer;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class TextRenderer implements Globals
{
    private final ModuleCache<FontMod> fontMod =
            Caches.getModule(FontMod.class);

    private CustomFontRenderer renderer =
        new CustomFontRenderer(new Font("Arial", Font.PLAIN, 17), true, true);

    public float drawStringWithShadow(String text, float x, float y, int color)
    {
        if (fontMod.isEnabled())
        {
            return renderer.drawStringWithShadow(text, x, y, color);
        }

        return mc.fontRenderer.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color)
    {
        if (fontMod.isEnabled())
        {
            return renderer.drawString(text, x, y, color);
        }

        return mc.fontRenderer.drawString(text, x, y, color, false);
    }

    public float drawString(String text,
                            float x,
                            float y,
                            int color,
                            boolean dropShadow)
    {
        if (fontMod.isEnabled())
        {
            if (dropShadow)
            {
                return renderer.drawStringWithShadow(text, x, y, color);
            }

            return renderer.drawString(text, x, y, color);
        }

        return mc.fontRenderer.drawString(text, x, y, color, dropShadow);
    }

    public void drawStringScaled(String text, float x, float y, int color, boolean dropShadow, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        drawString(text, x / scale, y / scale, color, dropShadow);
        GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
        GlStateManager.popMatrix();
    }

    public int getStringWidth(String text)
    {
        if (fontMod.isEnabled())
        {
            return renderer.getStringWidth(text);
        }

        return mc.fontRenderer.getStringWidth(text);
    }

    public float getStringWidthScaled(String text, float scale) {
        if (fontMod.isEnabled())
        {
            return renderer.getStringWidth(text) * scale;
        }

        return mc.fontRenderer.getStringWidth(text) * scale;
    }

    public int getStringHeightI()
    {
        if (fontMod.isEnabled())
        {
            return renderer.getHeight();
        }

        return mc.fontRenderer.FONT_HEIGHT;
    }

    // this is here to not break compatibility with plugins that still use it
    public float getStringHeight()
    {
        if (fontMod.isEnabled())
        {
            return renderer.getHeight();
        }

        return mc.fontRenderer.FONT_HEIGHT;
    }

    public float getStringHeightI(float scale)
    {
        if (fontMod.isEnabled())
        {
            return renderer.getHeight() * scale;
        }

        return mc.fontRenderer.FONT_HEIGHT * scale;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean metrics)
    {
        renderer = new CustomFontRenderer(font, antiAlias, metrics);
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth)
    {
        if (fontMod.isEnabled())
        {
            return renderer.wrapWords(str, wrapWidth);
        }

        return mc.fontRenderer.listFormattedStringToWidth(str, wrapWidth);
    }

}
