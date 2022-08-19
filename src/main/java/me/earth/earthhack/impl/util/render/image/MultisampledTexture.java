package me.earth.earthhack.impl.util.render.image;

import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

/**
 * Multisampled 2 dimensional texture for use as a framebuffer attachment.
 */
public class MultisampledTexture extends Texture
{

	private int samples;

	public MultisampledTexture(int samples, int width, int height)
	{
		super(width, height);
		this.samples = samples;
		init();
	}

	@Override
	public void init()
	{
		id = TextureUtil.glGenTextures();
		bind();
		/*GlStateManager.glTexParameteri(GL32.GL_TEXTURE_2D_MULTISAMPLE, GL12.GL_TEXTURE_MAX_LEVEL, 0);
		GlStateManager.glTexParameteri(GL32.GL_TEXTURE_2D_MULTISAMPLE, GL12.GL_TEXTURE_MIN_LOD, 0);
		GlStateManager.glTexParameteri(GL32.GL_TEXTURE_2D_MULTISAMPLE, GL12.GL_TEXTURE_MAX_LOD, 0);
		GlStateManager.glTexParameterf(GL32.GL_TEXTURE_2D_MULTISAMPLE, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);*/
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, GL11.GL_RGBA, width, height, true);
		unbind();
	}

	@Override
	public void bind()
	{
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, id);
	}

	@Override
	public void unbind()
	{
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
	}

}
