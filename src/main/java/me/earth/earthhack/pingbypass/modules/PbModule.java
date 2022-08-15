package me.earth.earthhack.pingbypass.modules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;

public class PbModule extends Module {
    private final Module module;
    private String displayInfo;

    public PbModule(Module module) {
        super(module.getName(), module.getCategory());
        for (Setting<?> setting : module.getSettings()) {
            if (getSetting(setting.getName()) != null) {
                continue;
            }

            Setting<?> copied = setting.copy();
            if (copied != null) {
                copied.setComplexity(setting.getComplexity());
                this.register(copied);
            }
        }

        this.module = module;
        this.setData(new PbData(this, module));
    }

    public Module getModule() {
        return module;
    }

    @Override
    public String getDisplayInfo() {
        return displayInfo;
    }

    public void setDisplayInfo(String displayInfo) {
        this.displayInfo = displayInfo;
    }

}
