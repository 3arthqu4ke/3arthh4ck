package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.api.module.data.DefaultData;

final class ToolTipsData extends DefaultData<ToolTips>
{
    public ToolTipsData(ToolTips module)
    {
        super(module);
        register(module.shulkers, "Displays tooltips for shulkers.");
        register(module.shulkerSpy,
                "Displays and saves shulkers held by other players.");
        register(module.own, "With Shulkerspy: displays your own held shulker.");
        register(module.peekBind, "While hovering over a shulker in " +
                "the gui, press this bind to peek into it.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Tooltips for shulkers etc.";
    }

}
