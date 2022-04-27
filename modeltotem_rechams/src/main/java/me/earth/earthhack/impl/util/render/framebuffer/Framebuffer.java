package me.earth.earthhack.impl.util.render.framebuffer;

import me.earth.earthhack.impl.util.render.GlObject;
import net.minecraft.client.renderer.OpenGlHelper;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author megyn
 */
public abstract class Framebuffer extends GlObject
{

	protected int width;
	protected int height;

	public Framebuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		setupFramebuffer(width, height);
	}

	/**
	 * Re-allocates the framebuffer with the given width and height.
	 * Has to update the width and height of the framebuffer.
	 * @param width new width of the framebuffer
	 * @param height new height of the framebuffer
	 */
	protected abstract void setupFramebuffer(int width, int height);

	/**
	 * Checks to make sure the framebuffer has been initialized properly.
	 * @return whether or not the framebuffer has been initialized.
	 */
	public boolean checkSetupFramebuffer()
	{
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, id);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			System.out.println("Error creating framebuffer: " + glCheckFramebufferStatus(GL_FRAMEBUFFER));
			return false;
		}
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
		return true;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

}
