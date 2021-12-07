package me.earth.earthhack.impl.managers.client.resource;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

@SuppressWarnings("unused")
public class PluginResourceLocation extends ResourceLocation
{
    private final String resourcePack;

    protected PluginResourceLocation(int unused, String resourcePack, String...resourceName)
    {
        super(unused, resourceName);
        this.resourcePack = resourcePack;
    }

    public PluginResourceLocation(String resourceName, String resourcePack)
    {
        super(resourceName);
        this.resourcePack = resourcePack;
    }

    public PluginResourceLocation(String namespaceIn, String pathIn, String resourcePack)
    {
        super(namespaceIn, pathIn);
        this.resourcePack = resourcePack;
    }

    public String getResourcePack()
    {
        return resourcePack;
    }

    public IResource toResource(String resourcePackNameIn,
                                ResourceLocation srResourceLocationIn,
                                InputStream resourceInputStreamIn,
                                InputStream mcmetaInputStreamIn,
                                MetadataSerializer srMetadataSerializerIn)
    {
        return new PluginResource(resourcePackNameIn,
                                  srResourceLocationIn,
                                  resourceInputStreamIn,
                                  mcmetaInputStreamIn,
                                  srMetadataSerializerIn);
    }

}
