package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.commands.hidden.HSettingCommand;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public enum RainbowEnum implements IColorIncrementor
{
    RainbowSpeed("<0 - 200>", TextColor.WHITE)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            float speed = (float)
             IncrementationUtil.crF(s.getRainbowSpeed(), 0.f, 200.f, !i);
            return () ->
            {
                s.setRainbowSpeed(speed);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s)
        {
            return MathUtil.round(s.getRainbowSpeed(), 2) + "";
        }
    },
    RainbowSaturation("<0 - 100>", TextColor.GOLD)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            float sat = (float)
             IncrementationUtil.crF(s.getRainbowSaturation(), 0.f, 100.f, !i);
            return () ->
            {
                s.setRainbowSaturation(sat);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s)
        {
            return MathUtil.round(s.getRainbowSaturation(), 2) + "";
        }
    },
    RainbowBrightness("<0 - 100>", TextColor.WHITE)
    {
        @Override
        public Runnable getCommand(ColorSetting s, boolean i, Module m)
        {
            float bright = (float)
             IncrementationUtil.crF(s.getRainbowBrightness(), 0.f, 100.f, !i);
            return () ->
            {
                s.setRainbowBrightness(bright);
                // TODO: updating might not be needed anymore
                HSettingCommand.update(s, m, null, true);
            };
        }

        @Override
        public String getValue(ColorSetting s)
        {
            return MathUtil.round(s.getRainbowBrightness(), 2) + "";
        }
    };

    private final String range;
    private final String color;

    RainbowEnum(String range, String color)
    {
        this.range = range;
        this.color = color;
    }

    public abstract String getValue(ColorSetting s);

    public String getRange()
    {
        return range;
    }

    public String getColor()
    {
        return color;
    }

}
