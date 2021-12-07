package me.earth.earthhack.impl.managers.client.resource;

import net.minecraft.client.resources.IResource;

@FunctionalInterface
public interface ResourceSupplier
{
    IResource get() throws ResourceException;
}
