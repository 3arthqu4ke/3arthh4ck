package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

//TODO: AutoBowSpam
public class BowSpam extends Module
{
    protected final Setting<Integer> delay   =
            register(new NumberSetting<>("Delay", 10, 0, 20));
    protected final Setting<Boolean> tpsSync =
            register(new BooleanSetting("TPS-Sync", true));
    protected final Setting<Boolean> bowBomb =
            register(new BooleanSetting("BowBomb", false));
    protected final Setting<Boolean> spam =
            register(new BooleanSetting("Spam", false));

    public BowSpam()
    {
        super("BowSpam", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.setData(new BowSpamData(this));
    }

}
