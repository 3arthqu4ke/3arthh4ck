package me.earth.plugins.phobosgui.gui.components;

import me.earth.earthhack.api.util.interfaces.Globals;

public class Item implements Globals
{
    private final String name;
    protected float x, y;
    protected int width, height;
    private boolean hidden;

    public Item(String name)
    {
        this.name = name;
    }

    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {}

    public void update() {}

    public void onKeyTyped(char typedChar, int keyCode) {}

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isHidden()
    {
        return !this.hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getName()
    {
        return name;
    }
}
