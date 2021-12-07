package me.earth.earthhack.impl.managers.client.resource;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.MetadataSerializer;

import java.io.InputStream;
import java.util.Objects;

public class PluginResourceSupplier implements ResourceSupplier
{
    private final PluginResourceLocation location;
    private final MetadataSerializer metadataSerializer;
    private final ClassLoader classLoader;

    public PluginResourceSupplier(PluginResourceLocation location,
                                  MetadataSerializer metadataSerializer,
                                  ClassLoader classLoader)
    {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.metadataSerializer = Objects.requireNonNull(metadataSerializer);
        this.location = location;
    }

    @Override
    public IResource get() throws ResourceException
    {
        String target = String.format("%s/%s/%s",
                "assets", location.getNamespace(), location.getPath());
        try
        {
            InputStream stream = classLoader.getResourceAsStream(target);
            if (stream == null)
            {
                throw new ResourceException(
                    "PluginResource: " + location + " had no InputStream!");
            }

            // TODO: no mc_meta Stream is rn!!!
            return location.toResource(location.getResourcePack(),
                                       location,
                                       stream,
                                       stream,
                                       metadataSerializer);
        }
        catch (Exception e)
        {
            throw new ResourceException(e);
        }
    }

}
