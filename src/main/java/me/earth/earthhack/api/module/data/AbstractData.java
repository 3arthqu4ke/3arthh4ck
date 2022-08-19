package me.earth.earthhack.api.module.data;

import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

import java.util.*;

// TODO: implement abstraction so this can be used for hud elements too!
public abstract class AbstractData<M extends SettingContainer> implements ModuleData<M>
{
    protected final Map<Setting<?>, String> descriptions = new HashMap<>();
    protected final Set<ModulePreset<M>> presets = new LinkedHashSet<>();
    protected final M module;

    public AbstractData(M module)
    {
        this.module = module;
    }

    @Override
    public Map<Setting<?>, String> settingDescriptions()
    {
        return descriptions;
    }

    @Override
    public Collection<ModulePreset<M>> getPresets()
    {
        return presets;
    }

    public void register(String setting, String description)
    {
        register(module.getSetting(setting), description);
    }

    public void register(Setting<?> setting, String description)
    {
        if (setting != null)
        {
            this.descriptions.put(setting, description);
        }
    }

}
