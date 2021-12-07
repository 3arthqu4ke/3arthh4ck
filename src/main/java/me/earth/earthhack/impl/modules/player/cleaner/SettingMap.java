package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.setting.Setting;

import java.util.Map;

public class SettingMap
{
    private final Setting<Integer> setting;
    private final Map<Integer, Integer> map;

    public SettingMap(Setting<Integer> setting, Map<Integer, Integer> map)
    {
        this.setting = setting;
        this.map = map;
    }

    public Map<Integer, Integer> getMap()
    {
        return map;
    }

    public Setting<Integer> getSetting()
    {
        return setting;
    }

}
