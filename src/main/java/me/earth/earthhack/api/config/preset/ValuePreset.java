package me.earth.earthhack.api.config.preset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValuePreset extends ModulePreset<Module>
{
    private final Map<String, JsonElement> values = new HashMap<>();

    public ValuePreset(String name, Module module, String description)
    {
        super(name, module, description);
    }

    public Map<String, JsonElement> getValues()
    {
        return values;
    }

    public JsonObject toJson()
    {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : values.entrySet())
        {
            object.add(entry.getKey(), entry.getValue());
        }

        return object;
    }

    @Override
    public void apply()
    {
        Module module = this.getModule();
        Set<Setting<?>> generated = GeneratedSettings.getGenerated(module);
        for (Setting<?> setting : generated)
        {
            module.unregister(setting);
        }

        GeneratedSettings.clear(module);
        Map.Entry<String, JsonElement> enabled = null;
        for (Map.Entry<String, JsonElement> entry : values.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase("Enabled"))
            {
                enabled = entry;
                continue;
            }

            if (module instanceof PingBypassModule
                && "Protocol".equalsIgnoreCase(entry.getKey())) {
                continue;
            }

            setSetting(module, entry);
        }
        // set enabled last, so all settings are to date when we enter onEnable.
        if (enabled != null && !(module instanceof PingBypassModule))
        {
            setSetting(module, enabled);
        }
    }

    public static ValuePreset snapshot(String name, Module module)
    {
        ValuePreset preset = new ValuePreset(name, module, "A config Preset.");
        for (Setting<?> setting : module.getSettings())
        {
            if (setting instanceof BindSetting)
            {
                continue;
            }

            JsonElement element = Jsonable.parse(setting.toJson());
            preset.getValues().put(setting.getName(), element);
        }

        return preset;
    }

    protected void setSetting(Module module,
                              Map.Entry<String, JsonElement> entry)
    {
        Setting<?> setting = module.getSettingConfig(entry.getKey());
        if (setting != null)
        {
            try
            {
                setting.fromJson(entry.getValue());
            }
            catch (Exception e)
            {
                System.out.println(module.getName()
                        + " : "
                        + setting.getName()
                        + " : Couldn't set value from json:");
                e.printStackTrace();

            }
        }
    }

}
