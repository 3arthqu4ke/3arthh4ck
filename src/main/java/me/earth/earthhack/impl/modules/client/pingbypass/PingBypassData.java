package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.module.data.DefaultData;

final class PingBypassData extends DefaultData<PingBypassModule>
{
    public PingBypassData(PingBypassModule module)
    {
        super(module);
        register("Port", "The port of the PingBypass " +
                "proxy you want to connect to.");
        register("Pings", "Delay in seconds for sending packets " +
                "that determine your ping to the PingBypass proxy.");
        register("NoRender", "Will make the PingBypass Proxy not render" +
                " anything to decrease the workload.");
        register("IP", "The IP of the PingBypass proxy that " +
                "you want to connect to.");
        register(module.allowEnable, "If you allow PingBypass servers" +
            " to enable the PingBypass module when you forgot to.");
        register(module.fixRotations,
                 "Currently in development but probably recommended! " +
                 "Important to make PingBypass correctly spoof your rotation.");
        register(module.alwaysUpdate, "Currently in development but should" +
            " also help PingBypass to correctly spoof positions/rotations.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "This module manages your PingBypass connection.";
    }

}
