package me.earth.earthhack.impl.modules.client.commands;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.Caches;

public class Commands extends Module
{
    private static final SettingCache<String, StringSetting, Commands> PREFIX =
        Caches.getSetting(Commands.class, StringSetting.class, "Prefix", "+");

    protected final Setting<Boolean> prefixBind =
        register(new BooleanSetting("PrefixBind", false));

    protected char prefixChar = '+';

    public Commands()
    {
        super("Commands", Category.Client);
        StringSetting prefix = register(new StringSetting("Prefix", "+"));
        register(new BooleanSetting("BackgroundGui", false));
        prefix.addObserver(s ->
        {
            if (!s.isCancelled())
            {
                if (s.getValue().length() == 1)
                {
                    prefixChar = s.getValue().charAt(0);
                }
                else
                {
                    prefixChar = '\0';
                }
            }
        });
        PREFIX.setContainer(this);
        PREFIX.set(prefix);
        Bus.EVENT_BUS.register(new KeyboardListener(this));
        this.setData(new CommandData(this));
    }

    public static void setPrefix(String prefix)
    {
        PREFIX.computeIfPresent(s -> s.setValue(prefix));
    }

    public static String getPrefix()
    {
        return !PREFIX.isPresent() ? "+" : PREFIX.getValue();
    }

}
