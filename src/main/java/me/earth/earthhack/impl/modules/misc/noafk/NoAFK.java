package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;

//TODO: Maybe AutoReply from File? CPacketTabComplete?
public class NoAFK extends Module
{
    /** Default Message for AutoReply. */
    private static final String DEFAULT =
            "I'm AFK! This message was brought to you by 3arthh4ck.";

    protected final Setting<Boolean> rotate    =
            register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> swing     =
            register(new BooleanSetting("Swing", true));
    protected final Setting<Boolean> sneak     =
            register(new BooleanSetting("Sneak", true));
    protected final Setting<Boolean> autoReply =
            register(new BooleanSetting("AutoReply", false));
    protected final Setting<String> message    =
            register(new StringSetting("Message", DEFAULT));
    protected final Setting<String> indicator  =
            register(new StringSetting("Indicator", " whispers: "));
    protected final Setting<String> reply      =
            register(new StringSetting("Reply", "/r "));
    protected final Setting<TextColor> color   =
            register(new EnumSetting<>("Color", TextColor.LightPurple));

    /** Timer to handle Swing Delay with */
    protected final StopWatch swing_timer = new StopWatch();
    /** Timer to handle Sneak Delay with */
    protected final StopWatch sneak_timer = new StopWatch();
    /** Handles sneaking. */
    protected boolean sneaking;

    public NoAFK()
    {
        super("NoAFK", Category.Misc);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerInput(this));
        this.setData(new NoAFKData(this));
    }

}
