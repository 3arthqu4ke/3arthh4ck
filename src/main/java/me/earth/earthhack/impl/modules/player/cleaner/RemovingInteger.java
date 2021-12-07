package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.util.helpers.addable.setting.Removable;

public class RemovingInteger extends NumberSetting<Integer> implements Removable
{
    static
    {
        ComponentFactory.register(RemovingInteger.class,
                                  RemovingIntegerComponent.FACTORY);
    }

    public RemovingInteger(String nameIn,
                           Integer initialValue,
                           Integer min,
                           Integer max)
    {
        super(nameIn, initialValue, min, max);
    }

    @Override
    public void remove()
    {
        if (this.container != null)
        {
            this.container.unregister(this);
        }
    }

    @Override
    public SettingResult fromString(String string)
    {
        if ("remove".equalsIgnoreCase(string))
        {
            remove();
            return new SettingResult(false, this.getName() + " was removed.");
        }

        return super.fromString(string);
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return super.getInputs(string) + " or <remove>";
        }

        if ("remove".startsWith(string.toLowerCase()))
        {
            return "remove";
        }

        return super.getInputs(string);
    }

}
