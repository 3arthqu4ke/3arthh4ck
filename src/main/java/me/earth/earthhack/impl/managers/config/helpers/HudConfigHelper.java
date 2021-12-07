package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.config.preset.ElementConfig;
import me.earth.earthhack.api.config.preset.HudValuePreset;
import me.earth.earthhack.api.config.preset.ValuePreset;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.Earthhack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HudConfigHelper extends AbstractConfigHelper<ElementConfig> {

    private final Register<HudElement> elements;

    public HudConfigHelper(Register<HudElement> elements)
    {
        super("hud", "hud");
        this.elements = elements;
    }

    @Override
    protected ElementConfig create(String name)
    {
        return ElementConfig.create(name.toLowerCase(), elements);
    }

    @Override
    protected JsonObject toJson(ElementConfig config)
    {
        JsonObject object = new JsonObject();
        for (HudValuePreset preset : config.getPresets())
        {
            JsonObject presetObject = preset.toJson();
            object.add(preset.getModule().getName(), presetObject);
        }

        return object;
    }

    @Override
    protected ElementConfig readFile(InputStream stream, String name)
    {
        JsonObject object = Jsonable.PARSER
                .parse(new InputStreamReader(stream))
                .getAsJsonObject();

        List<HudValuePreset> presets = new ArrayList<>(object.entrySet().size());
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            HudElement module = elements.getObject(entry.getKey());
            if (module == null)
            {
                Earthhack.getLogger().error("Config: Couldn't find element: "
                        + entry.getKey());
                continue;
            }

            HudValuePreset preset = new HudValuePreset(name, module, "A config Preset.");
            JsonObject element = entry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> s : element.entrySet())
            {
                boolean generated = module.getSetting(s.getKey()) == null;
                Setting<?> setting = module.getSettingConfig(s.getKey());
                if (setting == null)
                {
                    Earthhack.getLogger().error(
                            "Config: Couldn't find setting: " + s.getKey()
                                    + " in element: " + module.getName() + ".");
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

        ElementConfig config = new ElementConfig(name);
        config.setPresets(presets);
        return config;
    }

}
