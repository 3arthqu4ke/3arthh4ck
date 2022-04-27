package me.earth.plugins.phobosgui;

import me.earth.plugins.phobosgui.util.PhobosColorUtil;

import java.awt.*;

public class PhobosColorManager
{
    private static final PhobosColorManager INSTANCE = new PhobosColorManager();

    private float red;
    private float green;
    private float blue;
    private float alpha;
    private Color color;

    private PhobosColorManager()
    {
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 1.0f;
        this.color = new Color(this.red, this.green, this.blue, this.alpha);
    }

    public static PhobosColorManager getInstance()
    {
        return INSTANCE;
    }

    public Color getColor()
    {
        return this.color;
    }

    public int getColorAsInt()
    {
        return PhobosColorUtil.toRGBA(this.color);
    }

    public int getColorAsIntFullAlpha()
    {
        return PhobosColorUtil.toRGBA(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 255));
    }

    public int getColorWithAlpha(int alpha)
    {
        return PhobosColorUtil.toRGBA(new Color(this.red, this.green, this.blue, alpha / 255.0f));
    }

    public void setColor(float red, float green, float blue, float alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.updateColor();
    }

    public void updateColor()
    {
        this.setColor(new Color(this.red, this.green, this.blue, this.alpha));
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setColor(int red, int green, int blue, int alpha)
    {
        this.red = red / 255.0f;
        this.green = green / 255.0f;
        this.blue = blue / 255.0f;
        this.alpha = alpha / 255.0f;
        this.updateColor();
    }

    public void setRed(float red)
    {
        this.red = red;
        this.updateColor();
    }

    public void setGreen(float green)
    {
        this.green = green;
        this.updateColor();
    }

    public void setBlue(float blue)
    {
        this.blue = blue;
        this.updateColor();
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
        this.updateColor();
    }

}
