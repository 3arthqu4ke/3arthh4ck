package me.earth.earthhack.pingbypass.modules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.client.SimpleData;

import java.util.Map;

public class PbData extends DefaultData<PbModule> {
    private final String description;
    private final int color;

    public PbData(PbModule module, Module from) {
        super(module);
        ModuleData<?> data = from.getData();
        if (data == null) {
            data = new SimpleData(from, "A " + from.getCategory().toString() + " module.");
        }

        for (Map.Entry<Setting<?>, String> entry : data.settingDescriptions().entrySet()) {
            Setting<?> setting = module.getSetting(entry.getKey().getName());
            if (setting != null) {
                this.descriptions.put(setting, entry.getValue());
            }
        }

        this.description = data.getDescription();
        this.color = data.getColor();
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
