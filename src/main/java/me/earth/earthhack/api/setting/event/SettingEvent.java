package me.earth.earthhack.api.setting.event;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.api.setting.Setting;

public class SettingEvent<T> extends Event
{
    private final Setting<T> setting;
    private T value;

    public SettingEvent(Setting<T> setting, T value)
    {
        this.setting = setting;
        this.value = value;
    }

    public Setting<T> getSetting()
    {
        return setting;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public static class Post<T> extends SettingEvent<T> {
        public Post(Setting<T> setting, T value) {
            super(setting, value);
        }
    }

}
