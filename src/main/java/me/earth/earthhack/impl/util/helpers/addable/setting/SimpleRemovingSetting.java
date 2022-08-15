package me.earth.earthhack.impl.util.helpers.addable.setting;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.util.helpers.addable.setting.component.SimpleRemovingComponent;

public class SimpleRemovingSetting extends RemovingSetting<Boolean>
{
    static
    {
        ComponentFactory.register(SimpleRemovingSetting.class,
                                  SimpleRemovingComponent::new);
    }

    public SimpleRemovingSetting(String name)
    {
        super(name, true);
    }

    @Override
    public Setting<Boolean> copy() {
        return new SimpleRemovingSetting(getName());
    }

    @Override
    public void setValue(Boolean value, boolean withEvent)
    {
        SettingEvent<Boolean> event = onChange(new SettingEvent<>(this, value));
        if (!event.isCancelled() && !value)
        {
            super.remove();
        }
    }

}
