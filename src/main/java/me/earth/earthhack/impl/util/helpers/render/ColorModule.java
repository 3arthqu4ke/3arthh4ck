package me.earth.earthhack.impl.util.helpers.render;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.ColorSetting;

import java.awt.*;

public class ColorModule extends Module
{
    public final ColorSetting color =
            register(new ColorSetting("Color", new Color(255, 255, 255, 240)));

    public ColorModule(String name, Category category)
    {
        super(name, category);
    }

}
