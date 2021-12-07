package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.util.text.TextColor;

public class BindComponent extends DefaultComponent<Bind, BindSetting>
{
    public BindComponent(BindSetting setting)
    {
        super(setting);
    }

    @Override
    public String getText()
    {
        return super.getText() + TextColor.GRAY;
    }

}
