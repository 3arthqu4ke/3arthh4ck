package me.earth.earthhack.api.cache;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

/**
 * Cache/Proxy for a {@link Setting}.
 *
 * @param <T> the type of the settings values.
 * @param <S> the type of setting.
 * @param <E> the type of the container.
 */
public class SettingCache<T, S extends Setting<T>, E extends SettingContainer>
        extends Cache<S>
{
    private final Cache<E> container;
    private T defaultValue;

    private SettingCache(Cache<E> container)
    {
        this.container = container;
    }

    public void setDefaultValue(T value)
    {
        this.defaultValue = value;
    }

    public T getValue()
    {
        return returnIfPresent(Setting::getValue, defaultValue);
    }

    public E getContainer()
    {
        return container.get();
    }

    public void setContainer(E container)
    {
        this.container.set(container);
    }

    /**
     * Creates a new SettingCache that uses the Module from the given
     * ModuleCache to get the setting by name and class.
     *
     * @param name the name of the setting to get.
     * @param type the type of the setting to get.
     * @param module the module the setting is registered in.
     * @param defaultValue the default value to return.
     * @param <T> the type of the settings values.
     * @param <S> the type of setting.
     * @param <E> the type of module.
     * @return a new SettingCache for the setting of the given name.
     */
    @SuppressWarnings("unchecked")
    public static  <T, S extends Setting<T>, E extends Module>
        SettingCache<T, S, E> newModuleSettingCache(String name,
                                                    Class<?> type,
                                                    Cache<E> module,
                                                    T defaultValue)
    {
        Class<S> converted = (Class<S>) type;
        SettingCache<T, S, E> result = new SettingCache<>(module);
        result.setDefaultValue(defaultValue);
        result.getter = () ->
            result.container.returnIfPresent(c ->
                    c.getSetting(name, converted), null);

        return result;
    }

}
