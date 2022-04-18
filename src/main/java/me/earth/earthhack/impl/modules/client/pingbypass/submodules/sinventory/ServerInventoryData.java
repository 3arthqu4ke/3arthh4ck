package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory;

import me.earth.earthhack.api.module.data.DefaultData;

final class ServerInventoryData extends DefaultData<ServerInventory>
{
    public ServerInventoryData(ServerInventory module)
    {
        super(module);
        register("Delay", "The Delay in seconds to resync" +
                " your Inventory.");
    }

    @Override
    public int getColor()
    {
        return 0xffff0000;
    }

    @Override
    public String getDescription()
    {
        return "Resyncs your Inventory with PingBypass.";
    }

}
