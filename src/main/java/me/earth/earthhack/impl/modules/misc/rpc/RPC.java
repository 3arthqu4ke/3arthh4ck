package me.earth.earthhack.impl.modules.misc.rpc;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.discord.DiscordPresence;

public class RPC extends Module
{

    public final Setting<String> state =
            register(new StringSetting("State", "3arfH4ck :3"));
    public final Setting<String> details =
            register(new StringSetting("Details", "3arfH4ck :3"));
    public final Setting<String> largeImageKey =
            register(new StringSetting("LargeImageKey", "earthhack"));
    public final Setting<String> smallImageKey =
            register(new StringSetting("SmallImageKey", "Da greatest"));
    public final Setting<Boolean> customDetails =
            register(new BooleanSetting("CustomDetails", false));
    public final Setting<Boolean> showIP =
            register(new BooleanSetting("ShowIP", false));
    public final Setting<Boolean> froggers =
            register(new BooleanSetting("Froggers", false));

    public RPC()
    {
        super("RPC", Category.Misc);
    }

    protected void onEnable()
    {
        DiscordPresence.start();
    }

    protected void onDisable()
    {
        DiscordPresence.stop();
    }

}
