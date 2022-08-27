package me.earth.earthhack.impl.modules.player.multitask;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.core.mixins.MixinMinecraft;
import me.earth.earthhack.impl.util.client.SimpleData;

/**
 * Allows you to place while breaking.
 * <p>
 * {@link MixinMinecraft}.
 */
public class MultiTask extends Module
{
    public MultiTask()
    {
        super("MultiTask", Category.Player);
        this.setData(new SimpleData(this,
                "Allows you to eat while mining for example."));
    }

}
