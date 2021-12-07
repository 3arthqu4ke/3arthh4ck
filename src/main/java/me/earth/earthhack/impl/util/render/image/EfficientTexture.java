package me.earth.earthhack.impl.util.render.image;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Texture that does not store pixel data on the cpu.
 * Should be used when storing large amounts of textures is necessary.
 * Will be replaced when a more modern rendering pipeline is introduced for rendering stuff.
 * @author megyn
 */
public class EfficientTexture extends AbstractTexture
{
    private int[] textureData;
    /** width of this icon in pixels */
    private final int width;
    /** height of this icon in pixels */
    private final int height;

    public EfficientTexture(BufferedImage bufferedImage)
    {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.textureData, 0, bufferedImage.getWidth());
        this.updateEfficientTexture();
    }

    public EfficientTexture(int textureWidth, int textureHeight)
    {
        this.width = textureWidth;
        this.height = textureHeight;
        this.textureData = new int[textureWidth * textureHeight];
        TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
    }

    private void updateEfficientTexture()
    {
        TextureUtil.uploadTexture(this.getGlTextureId(), this.textureData, this.width, this.height);
        textureData = new int[0];
    }

}
