package me.earth.earthhack.api.util.interfaces;

/**
 * An Interface for all Objects that can have a name.
 * For most use-cases in this client the name should
 * never change.
 */
public interface Nameable
{
    /** @return the name for this Object. */
    String getName();

}
