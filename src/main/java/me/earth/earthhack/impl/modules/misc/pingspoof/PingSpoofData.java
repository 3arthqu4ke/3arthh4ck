package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.module.data.DefaultData;

final class PingSpoofData extends DefaultData<PingSpoof>
{
    public PingSpoofData(PingSpoof module)
    {
        super(module);
        register(module.delay, "By how much you want to spoof your ping.");
        register(module.keepAlive, "Default PingSpoof.");
        register(module.transactions, "Crystalpvp.cc PingSpoof bypass.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to spoof your ping.";
    }

}
