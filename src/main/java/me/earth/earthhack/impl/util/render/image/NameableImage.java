package me.earth.earthhack.impl.util.render.image;

import me.earth.earthhack.api.util.interfaces.Nameable;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class NameableImage implements Nameable
{

    private final String name;
    private final EfficientTexture texture;

    public NameableImage(EfficientTexture texture, String name)
    {
        this.name = name;
        this.texture = texture;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public EfficientTexture getTexture()
    {
        return texture;
    }

}
