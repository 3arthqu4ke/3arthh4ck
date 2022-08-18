package me.earth.earthhack.impl.gui.font;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class CustomFont
{
    private static final ModuleCache<FontMod> FONT =
        Caches.getModule(FontMod.class);
    private static final int IMG_SIZE = 512;

    protected CharData[] charData = new CharData[256];
    protected Font font;

    protected boolean antiAlias;
    protected boolean fractionalMetrics;

    protected int fontHeight = -1;
    protected int charOffset = 0;

    protected DynamicTexture tex;


    public CustomFont(Font font, boolean antiAlias, boolean fractionalMetrics)
    {
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        tex = setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected DynamicTexture setupTexture(Font font,
                                          boolean antiAlias,
                                          boolean fractionalMetrics,
                                          CharData[] chars)
    {
        BufferedImage img =
                generateFontImage(font, antiAlias, fractionalMetrics, chars);

        try
        {
            return new DynamicTexture(img);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    protected BufferedImage generateFontImage(Font font,
                                              boolean antiAlias,
                                              boolean fractionalMetrics,
                                              CharData[] chars)
    {
        BufferedImage bufferedImage =
            new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, IMG_SIZE, IMG_SIZE);
        g.setColor(Color.WHITE);

        g.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                fractionalMetrics
                        ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
                        : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                antiAlias
                        ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                        : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                antiAlias
                        ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);

        FontMetrics fontMetrics = g.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;

        for (int i = 0; i < chars.length; i++)
        {
            char ch = (char) i;
            CharData charData = new CharData();
            Rectangle2D dimensions =
                    fontMetrics.getStringBounds(String.valueOf(ch), g);

            charData.width = (dimensions.getBounds().width + 8);
            charData.height = dimensions.getBounds().height;

            if (positionX + charData.width >= IMG_SIZE)
            {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }

            if (charData.height > charHeight)
            {
                charHeight = charData.height;
            }

            charData.storedX = positionX;
            charData.storedY = positionY;

            if (charData.height > this.fontHeight)
            {
                this.fontHeight = charData.height;
            }

            chars[i] = charData;
            g.drawString(String.valueOf(ch),
                            positionX + 2,
                            positionY + fontMetrics.getAscent());

            positionX += charData.width;
        }

        return bufferedImage;
    }

    public void drawChar(CharData[] chars, char c, float x, float y)
    {
        try
        {
            drawQuad(x,
                     y,
                     chars[c].width,
                     chars[c].height,
                     chars[c].storedX,
                     chars[c].storedY,
                     chars[c].width,
                     chars[c].height);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void drawQuad(float x,
                            float y,
                            float width,
                            float height,
                            float srcX,
                            float srcY,
                            float srcWidth,
                            float srcHeight)
    {
        float rSRCX = srcX / IMG_SIZE;
        float rSRCY = srcY / IMG_SIZE;
        float rSRCW = srcWidth / IMG_SIZE;
        float rSRCH = srcHeight / IMG_SIZE;

        GL11.glTexCoord2f(rSRCX + rSRCW, rSRCY);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(rSRCX, rSRCY);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(rSRCX, rSRCY + rSRCH);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(rSRCX, rSRCY + rSRCH);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(rSRCX + rSRCW, rSRCY + rSRCH);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(rSRCX + rSRCW, rSRCY);
        GL11.glVertex2d(x + width, y);
    }

    public int getStringHeight(String text)
    {
        return getHeight();
    }

    public int getHeight()
    {
        if (FONT.isPresent() && FONT.get().changeHeight.getValue())
        {
            return (fontHeight - FONT.get().heightSub.getValue())
                            / FONT.get().heightFactor.getValue()
                            + FONT.get().heightAdd.getValue();
        }

        return (fontHeight - 8) / 2;
    }

    public int getStringWidth(String text)
    {
        int width = 0;
        for (char c : text.toCharArray())
        {
            if (c < charData.length)
            {
                width += charData[c].width - 8 + charOffset;
            }
        }

        return width / 2;
    }

    public boolean isAntiAlias()
    {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias)
    {
        if (this.antiAlias != antiAlias)
        {
            this.antiAlias = antiAlias;

            tex = setupTexture(font,
                                antiAlias,
                                fractionalMetrics,
                                charData);
        }
    }

    public boolean isFractionalMetrics()
    {
        return this.fractionalMetrics;
    }

    public void setFractionalMetrics(boolean fractionalMetrics)
    {
        if (this.fractionalMetrics != fractionalMetrics)
        {
            this.fractionalMetrics = fractionalMetrics;

            tex = setupTexture(font,
                                antiAlias,
                                fractionalMetrics,
                                charData);
        }
    }

    public Font getFont()
    {
        return this.font;
    }

    public void setFont(Font font)
    {
        this.font = font;
        tex = setupTexture(font,
                            antiAlias,
                            fractionalMetrics,
                            charData);
    }

}
