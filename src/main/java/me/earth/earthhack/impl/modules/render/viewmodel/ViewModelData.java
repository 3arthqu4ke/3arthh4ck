package me.earth.earthhack.impl.modules.render.viewmodel;

import me.earth.earthhack.api.module.data.DefaultData;

final class ViewModelData extends DefaultData<ViewModel>
{
    public ViewModelData(ViewModel module)
    {
        super(module);
        register(module.noSway, "Removes sway animation while the camera moves.");
        register(module.swingSpeed, "The speed at which your hand swings. 6 is default speed.");
        register(module.offX, "Rotates your Offhand around on the X-Axis.");
        register(module.offY, "Set the height of your Offhand.");
        register(module.mainX, "Rotates your mainhand around on the X-Axis.");
        register(module.mainY, "Set the height of your mainhand.");
        register(module.xScale, "Lower values mean off- and mainhand " +
                "are further together.");
        register(module.yScale, "Lower values mean off- and" +
                " mainhand are higher.");
        register(module.zScale, "Lower values mean off- and mainhand " +
                "are closer to you.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to change your Viewmodel.";
    }

}
