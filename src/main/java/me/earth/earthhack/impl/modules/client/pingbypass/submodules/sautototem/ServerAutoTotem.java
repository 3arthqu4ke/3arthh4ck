package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassSubmodule;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

public class ServerAutoTotem extends PingBypassSubmodule
{
    private int count = 0;

    public ServerAutoTotem(PingBypassModule pingBypass)
    {
        super(pingBypass, "S-AutoTotem", Category.Client);

        register(new NumberSetting<>("Health", 14.5f, 0.0f, 36.0f));
        register(new NumberSetting<>("SafeHealth", 3.5f, 0.0f, 36.0f));
        register(new BooleanSetting("XCarry", false));

        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.setData(new ServerAutoTotemData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return Integer.toString(count);
    }

    protected void onTick()
    {
        if (mc.player != null)
        {
            count = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);
        }
    }

}
