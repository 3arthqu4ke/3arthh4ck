package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class BuildinPreset<M extends SettingContainer> extends ModulePreset<M>
{
    private final Map<Setting, Object> values = new HashMap<>();

    public BuildinPreset(String name, M module, String description)
    {
        super(name, module, description);
    }

    public <T> void add(Setting<T> setting, T value)
    {
        values.put(setting, value);
    }

    @SuppressWarnings("unchecked")
    public <T> void add(String setting, T value)
    {
        Setting<T> s = (Setting<T>) getModule().getSetting(setting);
        add(s, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply()
    {
        for (Map.Entry<Setting, Object> entry : values.entrySet())
        {
            entry.getKey().setValue(entry.getValue());
        }
    }

}
