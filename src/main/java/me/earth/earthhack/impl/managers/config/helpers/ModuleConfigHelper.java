package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.config.preset.ModuleConfig;
import me.earth.earthhack.api.config.preset.ValuePreset;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModuleConfigHelper extends AbstractConfigHelper<ModuleConfig>
{
    private final Register<Module> modules;

    public ModuleConfigHelper(Register<Module> mods)
    {
        this("module", "modules", mods);
    }

    public ModuleConfigHelper(String name, String path, Register<Module> mods)
    {
        super(name, path);
        this.modules = mods;
    }

    @Override
    protected ModuleConfig create(String name)
    {
        return ModuleConfig.create(name.toLowerCase(), modules);
    }

    @Override
    protected JsonObject toJson(ModuleConfig config)
    {
        JsonObject object = new JsonObject();
        for (ValuePreset preset : config.getPresets())
        {
            JsonObject presetObject = preset.toJson();
            object.add(preset.getModule().getName(), presetObject);
        }

        return object;
    }

    @Override
    protected ModuleConfig readFile(InputStream stream, String name)
    {
        JsonObject object = Jsonable.PARSER
                                    .parse(new InputStreamReader(stream))
                                    .getAsJsonObject();

        List<ValuePreset> presets = new ArrayList<>(object.entrySet().size());
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            Module module = modules.getObject(entry.getKey());
            if (module == null)
            {
                Earthhack.getLogger().error("Config: Couldn't find module: "
                                            + entry.getKey());
                continue;
            }

            ValuePreset preset =
                    new ValuePreset(name, module, "A config Preset.");

            JsonObject element = entry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> s : element.entrySet())
            {
                boolean generated = module.getSetting(s.getKey()) == null;
                Setting<?> setting = module.getSettingConfig(s.getKey());
                if (setting == null)
                {
                    Earthhack.getLogger().error(
                        "Config: Couldn't find setting: " + s.getKey()
                            + " in module: " + module.getName() + ".");
                    continue;
                }

                if ("Enabled".equalsIgnoreCase(setting.getName())
                    && module instanceof PingBypassModule) {
                    continue;
                }

                preset.getValues().put(setting.getName(), s.getValue());
                if (generated
                        && GeneratedSettings.getGenerated(module)
                                            .remove(setting))
                {
                    module.unregister(setting);
                }
            }

            presets.add(preset);
        }

        ModuleConfig config = new ModuleConfig(name);
        config.setPresets(presets);
        return config;
    }

}
