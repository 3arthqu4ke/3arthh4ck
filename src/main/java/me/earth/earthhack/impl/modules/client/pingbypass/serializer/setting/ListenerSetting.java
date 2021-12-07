package me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting;

import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.event.SettingEvent;

final class ListenerSetting implements Observer<SettingEvent<?>>
{
    private final SettingSerializer serializer;

    public ListenerSetting(SettingSerializer serializer)
    {
        this.serializer = serializer;
    }

    @Override
    public void onChange(SettingEvent<?> value)
    {
        serializer.onSettingChange(value);
    }

}
