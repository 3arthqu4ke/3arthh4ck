package me.earth.earthhack.impl.modules.client.customfont.mode;

import java.awt.*;

public enum FontStyle
{
    Plain(Font.PLAIN),
    Bold(Font.BOLD),
    Italic(Font.ITALIC),
    All(Font.BOLD | Font.ITALIC);

    private final int style;

    FontStyle(int style)
    {
        this.style = style;
    }

    public int getFontStyle()
    {
        return style;
    }

}

