package me.earth.earthhack.impl.modules.client.rotationbypass;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

public class Compatibility extends Module {
    private final Setting<Boolean> showRotations = register(
        new BooleanSetting("ShowRotations", false));

    public Compatibility() {
        super("Compatibility", Category.Client);
        setData(
            new SimpleData(this, "Makes Rotations compatible with Future."));
    }

    public boolean isShowingRotations() {
        return showRotations.getValue();
    }

}
