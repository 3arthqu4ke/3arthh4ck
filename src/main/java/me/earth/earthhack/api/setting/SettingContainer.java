package me.earth.earthhack.api.setting;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A container for settings. Supports getting
 * Settings by name, registering and removing them.
 */
public class SettingContainer
{
    private Map<String, Setting<?>> settings = new LinkedHashMap<>();

    /**
     * Registers the given Setting.
     *
     * @param setting the setting to register.
     * @param <T> the type of the settings values.
     * @param <S> the type of the setting.
     * @return the setting.
     */
    public <T, S extends Setting<T>> S register(S setting)
    {
        if (setting != null)
        {
            setting.setContainer(this);
            settings.put(setting.getName().toLowerCase(), setting);
            return setting;
        }

        return null;
    }

    /**
     * Unregisters the Setting that would be retrieved when
     * {@link SettingContainer#getSetting(String)}, would be
     * called for the given Settings name.
     *
     * @param setting the Setting to unregister.
     * @return <tt>null</tt> if the setting is <tt>null</tt> or if no
     *         setting with the given settings name was registered.
     */
    public Setting<?> unregister(Setting<?> setting)
    {
        return setting == null
                ? null
                : settings.remove(setting.getName().toLowerCase());
    }

    /**
     * @param name the name of the setting.
     * @return a setting with the given name that was registered,
     *         or <tt>null</tt>, if no such setting was registered.
     */
    public final Setting<?> getSetting(String name)
    {
        return settings.get(name.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T, S extends Setting<T>> S getSetting(String name, Class<?> clazz)
    {
        Setting<?> setting = settings.get(name.toLowerCase());
        if (clazz.isInstance(setting))
        {
            return (S) setting;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T, S extends Setting<T>> S getSetting(
            String name, Class<S> clazz, Class<T> type)
    {
        Setting<?> setting = settings.get(name.toLowerCase());
        if (clazz.isInstance(setting)
                && setting.getInitial().getClass() == type)
        {
            return (S) setting;
        }

        return null;
    }

    /**
     * Should be used by the config system to get settings.
     * This can be overridden allowing modules to register
     * Settings dynamically.
     */
    public Setting<?> getSettingConfig(String name)
    {
        return settings.get(name.toLowerCase());
    }

    /**
     * The Settings will be in the order they've been registered in.
     * @return an unmodifiable collection of all registered Settings.
     */
    public Collection<Setting<?>> getSettings()
    {
        return Collections.unmodifiableCollection(settings.values());
    }

    /**
     * Allows you to insert a Setting before another setting.
     *
     * @param setting the setting to register.
     * @param before the setting that should come
     *               right after the registered one.
     * @param <T> the type of the Settings value.
     * @param <S> the type of the Setting.
     * @return the setting.
     */
    public <T, S extends Setting<T>> S registerBefore(S setting,
                                                      Setting<?> before)
    {
        return registerAt(setting, before, true);
    }

    /**
     * Allows you to insert a Setting after another setting.
     *
     * @param setting the setting to register.
     * @param after the setting that should come
     *              right before the registered one.
     * @param <T> the type of the Settings value.
     * @param <S> the type of the Setting.
     * @return the setting.
     */
    public <T, S extends Setting<T>> S registerAfter(S setting,
                                                     Setting<?> after)
    {
        return registerAt(setting, after, false);
    }

    private <T, S extends Setting<T>> S registerAt(S setting,
                                                   Setting<?> target,
                                                   boolean before)
    {
        if (setting != null)
        {
            setting.setContainer(this);
            Map<String, Setting<?>> newSettings = new LinkedHashMap<>();
            for (Map.Entry<String, Setting<?>> entry : settings.entrySet())
            {
                boolean found = entry.getValue().equals(target);
                if (found && before)
                {
                    newSettings.put(setting.getName().toLowerCase(), setting);
                }

                newSettings.put(entry.getKey(), entry.getValue());

                if (found && !before)
                {
                    newSettings.put(setting.getName().toLowerCase(), setting);
                }
            }

            this.settings = newSettings;
            return setting;
        }

        return null;
    }

}
