package me.earth.earthhack.impl.gui.visibility;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.settings.NumberSetting;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class NumberPageBuilder extends PageBuilder<Integer>
{
    public NumberPageBuilder(SettingContainer container,
                             String name,
                             int pages)
    {
        super(container, new NumberSetting<>(name, 1, 1, pages));
    }

    public NumberPageBuilder addPage(int page, Setting<?> from, Setting<?> to)
    {
        return (NumberPageBuilder) super.addPage(v -> v == page, from, to);
    }

    public NumberPageBuilder addPage(int page, Setting<?>...settings)
    {
        return (NumberPageBuilder) super.addPage(v -> v == page, settings);
    }

    // TODO: In AntiPackets this made an Empty Page????
    public static NumberPageBuilder autoPage(SettingContainer container,
                                             String name,
                                             int settingsPerPage,
                                             Iterable<? extends Setting<?>> settings)
    {
        if (settingsPerPage <= 0)
        {
            throw new IllegalArgumentException(
                    "SettingsPerPage needs to be an integer bigger than 0!");
        }

        Map<Integer, Setting<?>[]> pages = new HashMap<>();
        int i = 0;
        int page = 1;
        Setting<?>[] current = new Setting<?>[settingsPerPage];
        for (Setting<?> setting : settings)
        {
            current[i] = setting;
            if (++i == settingsPerPage)
            {
                pages.put(page++, current);
                current = new Setting<?>[settingsPerPage];
                i = 0;
            }
        }

        if (current[0] != null)
        {
            pages.put(page, current);
        }

        NumberPageBuilder pageBuilder =
                new NumberPageBuilder(container, name, pages.size());

        for (Map.Entry<Integer, Setting<?>[]> entry : pages.entrySet())
        {
            pageBuilder.addPage(entry.getKey(), entry.getValue());
        }

        return pageBuilder;
    }

}
