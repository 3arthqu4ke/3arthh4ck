package me.earth.earthhack.impl.modules.client.server.util;

import me.earth.earthhack.impl.modules.client.server.api.ILogger;

public class SystemLogger implements ILogger
{
    @Override
    public void log(String message)
    {
        System.out.println(message);
    }

}