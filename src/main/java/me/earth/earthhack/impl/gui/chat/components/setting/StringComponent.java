package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.text.TextColor;

public class StringComponent extends DefaultComponent<String, StringSetting>
{
    public StringComponent(StringSetting setting)
    {
        super(setting);
    }

    @Override
    public String getText()
    {
        return setting.getName()
                + TextColor.GRAY
                + " : "
                + TextColor.GOLD;
    }

}
