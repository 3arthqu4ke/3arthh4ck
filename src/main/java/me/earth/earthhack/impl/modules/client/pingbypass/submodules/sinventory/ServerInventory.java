package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;

public class ServerInventory extends SimpleSubModule<PingBypass>
{
    public ServerInventory(PingBypass pingBypass)
    {
        super(pingBypass, "S-Inventory", Category.Client);
        register(new NumberSetting<>("Delay", 5, 1, 60));
        this.setData(new ServerInventoryData(this));
    }

}
