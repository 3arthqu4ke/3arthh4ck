package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

// TODO: THIS!
public class FontCommand extends Command
{
    private static final ModuleCache<FontMod>
     CUSTOM_FONT = Caches.getModule(FontMod.class);
    private static final SettingCache<String, StringSetting, FontMod> FONT =
     Caches.getSetting(FontMod.class, StringSetting.class, "Font", "Verdana");

    public FontCommand()
    {
        super(new String[][]{{"font"},
                             {"list",
                              "set",
                              "size",
                              "style",
                              "alias",
                              "metrics",
                              "shadow"}});
        CommandDescriptions.register(this, "Manage the CustomFont. (TODO)");
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.YELLOW
                                    + "Current Font is: "
                                    + TextColor.AQUA
                                    + FONT.getValue()
                                    + ".");
        }
        else
        {
            String arg = args[1];
            switch (arg.toLowerCase())
            {
                case "list":
                    CUSTOM_FONT.computeIfPresent(FontMod::sendFonts);
                    break;
                case "set":

                case "size":
                case "style":
                case "alias":
                case "metrics":
                case "shadow":
            }
        }
    }

}
