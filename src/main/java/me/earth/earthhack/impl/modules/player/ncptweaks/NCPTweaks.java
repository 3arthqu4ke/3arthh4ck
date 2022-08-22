package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class NCPTweaks extends Module
{
    protected final Setting<Boolean> eating =
            register(new BooleanSetting("Eating", true));
    protected final Setting<Boolean> moving =
            register(new BooleanSetting("Moving", true));
    protected final Setting<Boolean> packet =
            register(new BooleanSetting("Packet", true));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 50, 0, 500));
    protected final Setting<Boolean> resetNCP =
            register(new BooleanSetting("Reset-NCP", false));
    protected final Setting<Boolean> sneakEat =
            register(new BooleanSetting("Sneak-Eat", false));
    protected final Setting<Boolean> stopSpeed =
            register(new BooleanSetting("Stop-Speed", false));
    protected final BooleanSetting elytraFix =
            register(new BooleanSetting("Elytra-Fix", false));

    protected boolean speedStopped;
    
    public NCPTweaks()
    {
        super("NCPTweaks", Category.Player);
        this.listeners.add(new ListenerWindowClick(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerPosLook(this));
    }

    @Override
    protected void onDisable()
    {
        speedStopped = false;
    }

    public boolean isSpeedStopped()
    {
        return stopSpeed.getValue() && speedStopped;
    }
    
}
