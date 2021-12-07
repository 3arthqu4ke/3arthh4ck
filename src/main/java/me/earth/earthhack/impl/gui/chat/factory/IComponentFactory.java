package me.earth.earthhack.impl.gui.chat.factory;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;

/**
 * A factory that creates {@link SettingComponent}s.
 *
 * @param <E> the type of the settings value
 * @param <S> the type of setting
 */
public interface IComponentFactory<E, S extends Setting<E>>
{
    /**
     * Creates a new {@link SettingComponent}
     * based on the given {@link Setting}.
     *
     * @param s the setting to create the component from.
     * @return a SettingComponent for the setting.
     */
    SettingComponent<E, S> create(S s);

}
