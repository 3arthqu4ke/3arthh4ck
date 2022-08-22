package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.api.module.data.DefaultData;

final class LogoutSpotsData extends DefaultData<LogoutSpots>
{
    public LogoutSpotsData(LogoutSpots module)
    {
        super(module);
        register(module.message,
                "Informs you about players that log out/back in.");
        register(module.render, "Renders the logout spots.");
        register(module.friends, "Takes friends into account.");
        register(module.scale, "Scale of the Nametag above the LogoutSpot.");
        register(module.remove,
                 "Delay in seconds after which a LogoutSpot will get removed." +
            " Set to 0 to not remove LogoutSpots.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Highlights the places where players have logged out.";
    }

}
