package me.earth.earthhack.impl.util.helpers.render.data;

import me.earth.earthhack.api.module.data.AbstractData;
import me.earth.earthhack.impl.util.helpers.render.ColorModule;

public abstract class ColorModuleData<T extends ColorModule>
        extends AbstractData<T>
{
    public ColorModuleData(T module)
    {
        super(module);
        register(module.color, "The color to render with." +
                " An alpha value of 0 means no color at all.");
    }

}
