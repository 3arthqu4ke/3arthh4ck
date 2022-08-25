package me.earth.earthhack.impl.modules.misc.extratab;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.gui.MixinGuiPlayerTabOverlay;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ExtraTab extends Module
{
    private static final ModuleCache<Media> MEDIA =
        Caches.getModule(Media.class);

    protected final Setting<Integer> size =
            register(new NumberSetting<>("Size", 250, 0, 500));

    public ExtraTab()
    {
        super("ExtraTab", Category.Misc);
        register(new BooleanSetting("Download-Threads", false));
        register(new BooleanSetting("Ping", false));
        register(new BooleanSetting("Bars", true));
    }

    /**
     * {@link MixinGuiPlayerTabOverlay}
     *
     * @param defaultSize if off, the default size to return.
     * @return size of the player tab list.
     */
    public int getSize(int defaultSize)
    {
        return this.isEnabled() ? size.getValue() : defaultSize;
    }

    /**
     * {@link MixinGuiPlayerTabOverlay}
     *
     * @param info the player info.
     * @return name to display on the tab list.
     */
    public String getName(NetworkPlayerInfo info)
    {
        String name = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : ScorePlayerTeam.formatPlayerName(
                    info.getPlayerTeam(),
                    info.getGameProfile().getName());

        String finalName = name;
        name = MEDIA.returnIfPresent(m -> m.convert(finalName), name);

        if (this.isEnabled())
        {
            if (Managers.FRIENDS.contains(finalName))
            {
                return TextColor.AQUA + name;
            }
            else if (Managers.ENEMIES.contains(finalName))
            {
                return TextColor.RED + name;
            }
        }

        return name;
    }

}
