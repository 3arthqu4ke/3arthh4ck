package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.values.ValueComponent;

public class DefaultComponent<T, S extends Setting<T>>
        extends SettingComponent<T, S>
{
    @SuppressWarnings("ConstantConditions")
    public DefaultComponent(S setting)
    {
        super(setting);
        ValueComponent value = new ValueComponent(setting);
        value.getStyle().setClickEvent(this.getStyle().getClickEvent());
        value.getStyle().setHoverEvent(this.getStyle().getHoverEvent());
        this.appendSibling(value);
    }

}
