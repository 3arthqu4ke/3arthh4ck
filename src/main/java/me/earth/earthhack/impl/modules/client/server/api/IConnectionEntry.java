package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.api.util.interfaces.Nameable;

public interface IConnectionEntry extends Nameable
{
    default int getId()
    {
        return 0x00;
    }
}
