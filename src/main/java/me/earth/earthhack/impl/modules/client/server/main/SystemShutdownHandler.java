package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.modules.client.server.api.IShutDownHandler;

public class SystemShutdownHandler implements IShutDownHandler
{
    @Override
    public void disable(String message)
    {
        System.out.println(message);
        System.exit(0);
    }

}
