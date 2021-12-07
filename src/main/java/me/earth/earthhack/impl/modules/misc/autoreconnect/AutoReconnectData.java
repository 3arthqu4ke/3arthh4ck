package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoReconnectData extends DefaultData<AutoReconnect>
{
    public AutoReconnectData(AutoReconnect module)
    {
        super(module);
        register(module.delay,
                "After this delay in seconds passed you will be reconnected.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Automatically reconnects you after you got kicked.";
    }

}
