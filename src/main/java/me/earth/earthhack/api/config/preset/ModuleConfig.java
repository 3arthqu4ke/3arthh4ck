package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.util.IdentifiedNameable;

import java.util.ArrayList;
import java.util.List;

public class ModuleConfig extends IdentifiedNameable implements Config
{
    private List<ValuePreset> presets = new ArrayList<>();

    public ModuleConfig(String name)
    {
        super(name);
    }

    public void setPresets(List<ValuePreset> presets)
    {
        if (presets != null)
        {
            this.presets = presets;
        }
    }

    public List<ValuePreset> getPresets()
    {
        return presets;
    }

    @Override
    public void apply()
    {
        for (ModulePreset<Module> preset : presets)
        {
            preset.apply();
        }
    }

    public static ModuleConfig create(String name, Register<Module> modules)
    {
        ModuleConfig config = new ModuleConfig(name);
        for (Module module : modules.getRegistered())
        {
            ValuePreset preset = ValuePreset.snapshot(name, module);
            config.presets.add(preset);
        }

        return config;
    }

}
