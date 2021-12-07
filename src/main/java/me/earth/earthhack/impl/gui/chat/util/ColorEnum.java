package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.util.text.TextColor;

import java.awt.*;

public enum ColorEnum implements IColorIncrementor
{
    Red(TextColor.RED)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            int red = s.getRed();
            if (red == 0 && !i || red == 255 && i)
            {
                return () -> { };
            }

            red = (int) IncrementationUtil.crL(red, 0, 255, !i);
            Color c = new Color(red,
                                s.getGreen(),
                                s.getBlue(),
                                s.getAlpha());

            return () ->
            {
                s.setValue(c);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s)
        {
            return s.getRed();
        }
    },
    Green(TextColor.GREEN)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            int green = s.getGreen();
            if (green == 0 && !i || green == 255 && i)
            {
                return () -> { };
            }

            green = (int) IncrementationUtil.crL(green, 0, 255, !i);
            Color c = new Color(s.getRed(),
                    green,
                    s.getBlue(),
                    s.getAlpha());

            return () ->
            {
                s.setValue(c);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s)
        {
            return s.getGreen();
        }
    },
    Blue(TextColor.BLUE)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            int blue = s.getBlue();
            if (blue == 0 && !i || blue == 255 && i)
            {
                return () -> { };
            }

            blue = (int) IncrementationUtil.crL(blue, 0, 255, !i);
            Color c = new Color(s.getRed(),
                    s.getGreen(),
                    blue,
                    s.getAlpha());

            return () ->
            {
                s.setValue(c);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s)
        {
            return s.getBlue();
        }
    },
    Alpha(TextColor.WHITE)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            int alpha = s.getAlpha();
            if (alpha == 0 && !i || alpha == 255 && i)
            {
                return () -> { };
            }

            alpha = (int) IncrementationUtil.crL(alpha, 0, 255, !i);
            Color c = new Color(s.getRed(),
                    s.getGreen(),
                    s.getBlue(),
                    alpha);

            return () ->
            {
                s.setValue(c);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public int getValue(ColorSetting s)
        {
            return s.getAlpha();
        }
    };

    private final String textColor;

    ColorEnum(String textColor)
    {
        this.textColor = textColor;
    }

    public abstract int getValue(ColorSetting s);

    public String getTextColor()
    {
        return textColor;
    }

}
