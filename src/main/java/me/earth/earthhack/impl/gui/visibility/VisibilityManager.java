package me.earth.earthhack.impl.gui.visibility;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.modules.client.settings.SettingsModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the Visibility of Settings.
 */
// TODO: visibilities for PingBypassModules
@SuppressWarnings("unused")
public class VisibilityManager
{
    private static final VisibilitySupplier ALWAYS = () -> true;
    private final Map<Setting<?>, VisibilitySupplier> visibilities =
            new HashMap<>();

    public VisibilitySupplier getVisibility(Setting<?> setting)
    {
        return visibilities.getOrDefault(setting, ALWAYS);
    }

    public void registerVisibility(Setting<?> setting,
                                   VisibilitySupplier visibility)
    {
        if (visibility == null)
        {
            visibilities.remove(setting);
            return;
        }

        visibilities.compute(setting, (k, v) ->
        {
            if (v == null)
            {
                return visibility;
            }

            return visibility.compose(v);
        });
    }

    public boolean isVisible(Setting<?> setting)
    {
        return SettingsModule.shouldDisplay(setting)
            && getVisibility(setting).isVisible();
    }

}
