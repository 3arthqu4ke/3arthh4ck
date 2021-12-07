package me.earth.earthhack.impl.managers.config.util;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.IdentifiedNameable;
import me.earth.earthhack.impl.Earthhack;

import java.util.ArrayList;
import java.util.List;

public class BindConfig extends IdentifiedNameable implements Config
{
    private final List<BindWrapper> binds = new ArrayList<>();
    private final Register<Module> modules;

    public BindConfig(String name, Register<Module> modules)
    {
        super(name);
        this.modules = modules;
    }

    public void add(BindWrapper wrapper)
    {
        binds.add(wrapper);
    }

    public List<BindWrapper> getBinds()
    {
        return binds;
    }

    @Override
    public void apply()
    {
        for (BindWrapper wrapper : binds)
        {
            Module module = modules.getObject(wrapper.getModule());
            if (module == null)
            {
                Earthhack.getLogger().error("BindWrapper: Couldn't find module: "
                                         + wrapper.getModule() + ".");
                continue;
            }

            Setting<?> setting = module.getSettingConfig(wrapper.getName());
            if (setting == null)
            {
                Earthhack.getLogger().error("BindWrapper: Couldn't find setting: "
                                        + wrapper.getName() + " in module: "
                                        + wrapper.getModule() + ".");
                continue;
            }

            setting.fromString(wrapper.getValue());
        }
    }

    public static BindConfig create(String name, Register<Module> modules)
    {
        BindConfig config = new BindConfig(name, modules);
        for (Module module : modules.getRegistered())
        {
            for (Setting<?> setting : module.getSettings())
            {
                if (setting instanceof BindSetting)
                {
                    config.add(new BindWrapper(setting.getName(),
                                                    module.getName(),
                                                    setting.toJson()));
                }
            }
        }

        return config;
    }

}
