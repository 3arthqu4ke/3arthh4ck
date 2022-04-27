package me.earth.earthhack.impl.util.render.framebuffer;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Framebuffer with just a depth texture.
 */
public class DepthFramebuffer extends Framebuffer
{

	private int framebufferTexture = -1;
	private int depthTexture = -1;

	public DepthFramebuffer(int width, int height)
	{
		super(width, height);
	}

	@Override
	protected void setupFramebuffer(int width, int height)
	{
		// generate framebuffer
		id = OpenGlHelper.glGenFramebuffers();
		// generate texture
		framebufferTexture = TextureUtil.glGenTextures();
		// generate depth texture
		depthTexture = TextureUtil.glGenTextures();

		// setup framebuffer texture
		glBindTexture(GL_TEXTURE_2D, framebufferTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);

		// setup depth texture
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (IntBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);

		// finish framebuffer setup
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, id);
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, framebufferTexture, 0);
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, id);
		checkSetupFramebuffer();
	}

	public int getFramebufferTexture()
	{
		return framebufferTexture;
	}

	public int getDepthTexture()
	{
		return depthTexture;
	}

}
