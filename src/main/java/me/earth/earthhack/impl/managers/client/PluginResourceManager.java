package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.managers.client.resource.PluginResourceLocation;
import me.earth.earthhack.impl.managers.client.resource.PluginResourceSupplier;
import me.earth.earthhack.impl.managers.client.resource.ResourceException;
import me.earth.earthhack.impl.managers.client.resource.ResourceSupplier;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginResourceManager implements Globals
{
    private static final PluginResourceManager INSTANCE =
            new PluginResourceManager();

    private final Map<ResourceLocation, List<ResourceSupplier>> resourceMap;

    private PluginResourceManager()
    {
        this.resourceMap = new ConcurrentHashMap<>();
    }

    public static PluginResourceManager getInstance()
    {
        return INSTANCE;
    }

    public ResourceSupplier getSingleResource(ResourceLocation location)
    {
        List<ResourceSupplier> suppliers = resourceMap.get(location);
        if (suppliers == null || suppliers.size() != 1)
        {
            return null;
        }

        return suppliers.get(0);
    }

    public List<IResource> getPluginResources(ResourceLocation location)
    {
        List<IResource> result;
        List<ResourceSupplier> suppliers = resourceMap.get(location);

        if (suppliers != null)
        {
            Earthhack.getLogger().info("Found "
                + suppliers.size()
                + " custom ResourceLocation"
                + (suppliers.size() == 1 ? "" : "s")
                + " for "
                + location);

            result = new ArrayList<>(suppliers.size());
            for (ResourceSupplier supplier : suppliers)
            {
                if (supplier != null)
                {
                    try
                    {
                        IResource resource = supplier.get();
                        if (resource != null)
                        {
                            result.add(resource);
                        }
                    }
                    catch (ResourceException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            result = Collections.emptyList();
        }

        return result;
    }

    public void register(PluginResourceLocation r)
    {
        this.register(new ResourceLocation(r.getNamespace(), r.getPath()), r);
    }

    public void register(ResourceLocation location,
                         PluginResourceLocation resourceLocation)
    {
        Earthhack.getLogger().info("Adding custom ResourceLocation: "
                + location + " for: " + resourceLocation);

        ClassLoader loader = PluginManager.getInstance().getPluginClassLoader();
        if (loader == null)
        {
            throw new IllegalStateException("Plugin ClassLoader was null!");
        }

        MetadataSerializer mds = ((IMinecraft) mc).getMetadataSerializer();
        if (mds == null)
        {
            throw new IllegalStateException("MetadataSerializer was null!");
        }

        ResourceSupplier supplier =
            new PluginResourceSupplier(resourceLocation, mds, loader);

        register(location, supplier);
    }

    public void register(ResourceLocation location,
                         ResourceSupplier...resourceSuppliers)
    {
        List<ResourceSupplier> suppliers =
                resourceMap.computeIfAbsent(location, v -> new ArrayList<>());

        for (ResourceSupplier supplier : resourceSuppliers)
        {
            if (supplier != null)
            {
                suppliers.add(supplier);
            }
        }
    }

}
