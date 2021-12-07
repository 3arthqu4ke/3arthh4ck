package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.impl.core.ducks.render.ITextureManager;
import me.earth.earthhack.impl.util.render.image.EfficientTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(TextureManager.class)
public abstract class MixinTextureManager implements ITextureManager
{

    @Shadow @Final private Map<String, Integer> mapTextureCounters;

    @Shadow public abstract boolean loadTexture(ResourceLocation textureLocation, ITextureObject textureObj);

    @Override
    public ResourceLocation getEfficientTextureResourceLocation(String name, EfficientTexture texture)
    {
        Integer integer = mapTextureCounters.get(name);

        if (integer == null)
        {
            integer = Integer.valueOf(1);
        }
        else
        {
            integer = integer.intValue() + 1;
        }

        this.mapTextureCounters.put(name, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
        loadTexture(resourcelocation, texture);
        return resourcelocation;
    }

}
