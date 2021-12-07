package me.earth.earthhack.impl.util.helpers.render.data;

import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;

public abstract class BlockESPModuleData<T extends BlockESPModule>
        extends ColorModuleData<T>
{
    public BlockESPModuleData(T module)
    {
        super(module);
        register(module.height, "The height to render BlockESPs with." +
                " A value of 0 results in a flat ESP.");
        register(module.lineWidth, "The width of the lines to draw " +
                "the outline of the ESP with.");
        register(module.outline, "The Color to draw the outline with." +
                " An alpha value of 0 means no outline at all.");
    }

}
