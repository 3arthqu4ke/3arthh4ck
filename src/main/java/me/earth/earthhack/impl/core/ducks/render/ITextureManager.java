package me.earth.earthhack.impl.core.ducks.render;

import me.earth.earthhack.impl.util.render.image.EfficientTexture;
import net.minecraft.util.ResourceLocation;

public interface ITextureManager
{

    ResourceLocation getEfficientTextureResourceLocation(String name, EfficientTexture texture);

}
