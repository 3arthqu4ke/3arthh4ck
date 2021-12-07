package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class EntityControl extends Module
{
    protected final Setting<Boolean> control   =
            register(new BooleanSetting("Control", true));
    protected final Setting<Double> jumpHeight =
            register(new NumberSetting<>("JumpHeight", 0.7, 0.0, 2.0));
    protected final Setting<Boolean> noAI      =
            register(new BooleanSetting("NoAI", true));

    public EntityControl()
    {
        super("EntityControl", Category.Movement);
        this.listeners.add(new ListenerControl(this));
        this.listeners.add(new ListenerAI(this));
        this.listeners.add(new ListenerHorse(this));
        this.listeners.add(new ListenerTick(this));
        this.setData(new EntityControlData(this));
    }

}
