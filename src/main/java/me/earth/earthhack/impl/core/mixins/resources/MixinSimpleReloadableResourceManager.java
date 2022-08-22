package me.earth.earthhack.impl.core.mixins.resources;

import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.client.PluginResourceManager;
import me.earth.earthhack.impl.managers.client.resource.PluginResourceLocation;
import me.earth.earthhack.impl.managers.client.resource.PluginResourceSupplier;
import me.earth.earthhack.impl.managers.client.resource.ResourceException;
import me.earth.earthhack.impl.managers.client.resource.ResourceSupplier;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinSimpleReloadableResourceManager
{
    @Shadow
    @Final
    private MetadataSerializer rmMetadataSerializer;

    @Shadow
    @Final
    private Set<String> setResourceDomains;

    @Redirect(
        method = "getAllResources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/IResourceManager;getAllResources(Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;"))
    private List<IResource> getAllResourcesHook(IResourceManager iResourceManager, ResourceLocation location)
            throws IOException
    {
        List<IResource> list = iResourceManager.getAllResources(location);
        list.addAll(PluginResourceManager.getInstance().getPluginResources(location));
        return list;
    }

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void getResourceHook(ResourceLocation location,
                                 CallbackInfoReturnable<IResource> cir)
    {
        ResourceSupplier supplier;
        if (/*!Environment.hasForge()
            &&*/ !(location instanceof PluginResourceLocation)
            && location.getNamespace().equals("earthhack"))
        {
            location = new PluginResourceLocation(location.getNamespace()
                    + ":" + location.getPath(), "earthhack");
        }

        if (location instanceof PluginResourceLocation)
        {
            PluginResourceLocation loc = (PluginResourceLocation) location;
            ClassLoader classLoader = PluginManager.getInstance()
                                                   .getPluginClassLoader();
            if (classLoader == null)
            {
                throw new IllegalStateException("PluginClassLoader was null!");
            }

            supplier = new PluginResourceSupplier(
                    loc,
                    this.rmMetadataSerializer,
                    classLoader);
        }
        else
        {
            supplier = PluginResourceManager.getInstance()
                                            .getSingleResource(location);
        }

        if (supplier != null)
        {
            Earthhack.getLogger().info("Custom Resource detected: " + location);

            try
            {
                IResource resource = supplier.get();
                cir.setReturnValue(resource);
            }
            catch (ResourceException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Inject(method = "getResourceDomains", at = @At("HEAD"), cancellable = true)
    public void getResourceDomainsHook(CallbackInfoReturnable<Set<String>> cir)
    {
        Set<String> domains = this.setResourceDomains;
        domains.add("earthhack");
    }

}
