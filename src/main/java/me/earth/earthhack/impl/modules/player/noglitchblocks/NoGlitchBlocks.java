package me.earth.earthhack.impl.modules.player.noglitchblocks;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

/**
 * {@link me.earth.earthhack.forge.mixins.entity.MixinPlayerControllerMP}
 */
public class NoGlitchBlocks extends Module
{
    protected final Setting<Boolean> place  =
            register(new BooleanSetting("Place", true));
    protected final Setting<Boolean> crack  =
            register(new BooleanSetting("Break", true));
    protected final Setting<Boolean> ground =
            register(new BooleanSetting("Ground", false));

    public NoGlitchBlocks()
    {
        super("NoGlitchBlocks", Category.Player);
        this.listeners.add(new ListenerBlockDestroy(this));
        SimpleData data = new SimpleData(this,
                "Tries to prevent Glitchblocks.");
        data.register(place, "Prevents Glitchblocks when placing.");
        data.register(crack, "Prevents Glitchblocks when breaking blocks.");
        data.register(ground,
                "Always check for GlitchBlocks, not only when on the ground.");
        this.setData(data);
    }

    public boolean noPlace()
    {
        return isEnabled()
                && place.getValue()
                && (ground.getValue() || mc.player.onGround);
    }

    public boolean noBreak()
    {
        return isEnabled()
                && crack.getValue();
    }

}
