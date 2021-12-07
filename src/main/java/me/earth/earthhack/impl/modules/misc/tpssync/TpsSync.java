package me.earth.earthhack.impl.modules.misc.tpssync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.mixins.entity.living.player.MixinEntityPlayer;
import me.earth.earthhack.impl.core.mixins.entity.living.player.MixinPlayerControllerMP;

/**
 * {@link MixinPlayerControllerMP}.
 * {@link MixinEntityPlayer}.
 */
public class TpsSync extends Module
{
    public TpsSync()
    {
        super("TpsSync", Category.Player);
        register(new BooleanSetting("Attack", false));
        register(new BooleanSetting("Mine", false));
        // register(new BooleanSetting("Eat", false)); TODO
        this.setData(new TpsSyncData(this));
    }

}
