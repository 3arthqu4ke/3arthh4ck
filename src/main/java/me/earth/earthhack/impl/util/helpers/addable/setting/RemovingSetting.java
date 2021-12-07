package me.earth.earthhack.impl.util.helpers.addable.setting;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.event.SettingResult;

public abstract class RemovingSetting<T> extends Setting<T> implements Removable
{
    public RemovingSetting(String name, T initial)
    {
        super(name, initial);
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
    public void setValue(T value)
    {
        setValue(value, true);
    }

    @Override
    public void setValue(T value, boolean withEvent)
    {
        SettingEvent<T> event = onChange(new SettingEvent<>(this, value));
        if (!event.isCancelled())
        {
            remove();
        }
    }

    @Override
    public void fromJson(JsonElement element)
    {
        /* Nothing */
    }

    @Override
    public SettingResult fromString(String string)
    {
        if ("remove".equalsIgnoreCase(string))
        {
            remove();
            return new SettingResult(false, this.getName() + " was removed.");
        }

        return new SettingResult(false, "Possible input: \"remove\".");
    }

    @Override
    public String getInputs(String string)
    {
        if (string == null || string.isEmpty())
        {
            return "<remove>";
        }

        if ("remove".startsWith(string.toLowerCase()))
        {
            return "remove";
        }

        return "";
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof RemovingSetting<?>)
        {
            return this.name.equalsIgnoreCase(((RemovingSetting<?>) o).name)
                    && this.container != null
                    && this.container
                           .equals(((RemovingSetting<?>) o)
                           .getContainer());
        }

        return false;
    }

}
