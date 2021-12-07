package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;

public class NoRotate extends Module
{
    protected final Setting<Boolean> noForceLook =
            register(new BooleanSetting("NoForceLook", false));
    protected final Setting<Boolean> async =
            register(new BooleanSetting("Async", false));
    protected final Setting<Boolean> noSpoof =
            register(new BooleanSetting("NoThrowableSpoof", false));

    public NoRotate()
    {
        super("NoRotate", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
    }

}
