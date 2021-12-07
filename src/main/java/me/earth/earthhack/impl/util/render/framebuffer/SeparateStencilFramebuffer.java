package me.earth.earthhack.impl.util.render.framebuffer;

/**
 * Utilizes functionality introduced in OpenGL 4.4 that allows for framebuffers to have separate stencil and depth texture attachments.
 * This should rarely be used.
 */
public class SeparateStencilFramebuffer extends Framebuffer
{

	private int framebufferTexture = -1;
	private int depthTexture = -1;
	private int stencilTexture = -1;

	public SeparateStencilFramebuffer(int width, int height)
	{
		super(width, height);
	}

	@Override
	protected void setupFramebuffer(int width, int height)
	{

	}

}
