package me.earth.earthhack.api.config.preset;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.hud.HudElement;

public abstract class ElementPreset implements Config
{
    private final HudElement element;
    private final String name;
    private final String description;

    public ElementPreset(String name, HudElement element, String description)
    {
        this.name = name;
        this.element = element;
        this.description = description;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public HudElement getElement()
    {
        return element;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ElementPreset)
        {
            ElementPreset other = (ElementPreset) o;
            return other.name.equals(this.name)
                    && other.element.equals(this.element);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

}
