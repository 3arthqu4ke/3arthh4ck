package me.earth.earthhack.impl.modules.client.pingbypass.serializer.setting;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;

public class PbSettingSerializer extends SettingSerializer {
    public PbSettingSerializer(PingBypassModule module) {
        super(module);
    }

    public void addModules(ModuleManager moduleManager) {
        moduleManager.getRegistered().stream().filter(m -> !(m instanceof ClickGui)).forEach(modules::add);
        this.init(new ListenerSetting(this));
    }

    @Override
    protected boolean isSettingSerializable(Setting<?> setting)
    {
        return true;
    }

}
