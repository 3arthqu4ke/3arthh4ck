package me.earth.earthhack.api.register;

import me.earth.earthhack.api.register.exception.CantUnregisterException;

/**
 * An interface for classes that can be "registered" somewhere,
 * whatever that might be.
 */
public interface Registrable
{
    /**
     * Can be called when this Registrable
     * is registered somewhere.
     */
    default void onRegister()
    {
        /* Implement behaviour on Registration here. */
    }

    /**
     * Called when this is registered somewhere.
     *
     * @throws CantUnregisterException if this can't be unregistered.
     */
    default void onUnRegister() throws CantUnregisterException
    {
        /* Implement behaviour on UnRegistration here. */
    }

}
