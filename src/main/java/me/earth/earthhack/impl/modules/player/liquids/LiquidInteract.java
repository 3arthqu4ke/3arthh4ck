package me.earth.earthhack.impl.modules.player.liquids;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.core.mixins.block.MixinBlockLiquid;
import me.earth.earthhack.impl.util.client.SimpleData;

/**
 * {@link MixinBlockLiquid}.
 */
//TODO: Make this better so we can place while we are inside water?
public class LiquidInteract extends Module
{
    public LiquidInteract()
    {
        super("LiquidInteract", Category.Player);
        this.setData(new SimpleData(this, "Allows you to place on liquids"));
    }

}
