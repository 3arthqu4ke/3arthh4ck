package me.earth.earthhack.api.util.interfaces;

import me.earth.earthhack.api.module.util.Hidden;

/**
 * An interface for Objects that can be {@link Hidden}.
 */
public interface Hideable
{
    /** Sets the {@link Hidden} state for this Object. */
    void setHidden(Hidden hidden);

    /** @return the {@link Hidden} state for this Object. */
    Hidden isHidden();

}
