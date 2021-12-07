package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.module.data.DefaultData;

final class PingBypassData extends DefaultData<PingBypass>
{
    protected PingBypassData(PingBypass module)
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
