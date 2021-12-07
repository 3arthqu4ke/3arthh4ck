package me.earth.earthhack.impl.modules.client.hud.modes;

import me.earth.earthhack.impl.util.text.TextColor;

public enum HudRainbow
{
    None(""),
    Horizontal(TextColor.RAINBOW_PLUS),
    Vertical(TextColor.RAINBOW_MINUS),
    Static("");

    private final String color;

    HudRainbow(String color)
    {
        this.color = color;
    }

    public String getColor()
    {
        return color;
    }

}
