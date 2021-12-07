package me.earth.earthhack.impl.modules.misc.portals;

import me.earth.earthhack.api.module.data.DefaultData;

final class PortalsData extends DefaultData<Portals>
{
    public PortalsData(Portals module)
    {
        super(module);
        register(module.godMode, "Never use outside a portal." +
                " Step through a portal, when you come out on" +
                " the other side you can't move, but you" +
                " also can't be damaged.");
        register("Chat", "Allows you to chat while being inside portals.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Tweaks for portals.";
    }

}