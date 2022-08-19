package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.setting.SettingContainer;

// TODO: this class could probably be generified and any references to the module class could be replaced with a generic extending SettingContainer
// I'm too lazy to do the above myself atm!
// Nvm I wasn't too lazy
public abstract class ModulePreset<T extends SettingContainer> implements Config
{
    private final T module;
    private final String name;
    private final String description;

    public ModulePreset(String name, T module, String description)
    {
        this.name = name;
        this.module = module;
        this.description = description;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public T getModule()
    {
        return module;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ModulePreset)
        {
            ModulePreset<?> other = (ModulePreset<?>) o;
            return other.name.equals(this.name)
                    && other.module.equals(this.module);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

}
