package me.earth.earthhack.impl.gui.chat.components.values;

import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.util.text.TextColor;

public class EnumHoverComponent<E extends Enum<E>>
        extends SettingComponent<E, EnumSetting<E>>
{
    public EnumHoverComponent(EnumSetting<E> setting)
    {
        super(setting);
    }

    @Override
    public String getText()
    {
        return  TextColor.AQUA
                + setting.getValue().name()
                + TextColor.GRAY
                + " -> "
                + TextColor.WHITE
                + EnumHelper.next(setting.getValue()).name();
    }

}
