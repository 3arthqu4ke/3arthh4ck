package me.earth.earthhack.impl.gui.module;

import me.earth.earthhack.api.setting.Setting;

/**
 * For better structuring in the gui. (When we actually have one)...
 * Adds a header over the given setting.
 */
public class SettingHeader
{
    private final String name;
    private final Setting<?> firstChild;
    private String description;

    public SettingHeader(String nameIn, Setting<?> firstChild)
    {
        this.name = nameIn;
        this.firstChild = firstChild;
    }

    public String getName()
    {
        return name;
    }

    public Setting<?> getSetting()
    {
        return firstChild;
    }

    public String getDescription()
    {
        return description;
    }

    public SettingHeader setDescription(String description)
    {
        this.description = description;
        return this;
    }

}
