package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;

public interface IColorIncrementor
{
    Runnable getCommand(ColorSetting s, boolean i, Module m);
}
