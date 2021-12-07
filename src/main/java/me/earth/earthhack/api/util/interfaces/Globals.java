package me.earth.earthhack.api.util.interfaces;

import net.minecraft.client.Minecraft;

import java.util.Objects;

/**
 * Convenience interface so we don't have to
 * {@link Minecraft#getMinecraft()} everywhere.
 */
public interface Globals
{
    /** Minecraft Instance. */
    Minecraft mc = Objects.requireNonNull(Minecraft.getMinecraft());

}
