package me.earth.earthhack.impl.util.render.framebuffer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Framebuffer that uses readable textures for sampling with more modern opengl versions
 * @author megyn
 * TODO: options for building a framebuffer with options for using render buffers, textures, depth, and stenciling.
 */
public class TextureFramebuffer
{

    private int framebuffer = -1;
    private int framebufferTexture = -1;
    private int depthStencilTexture = -1;
    private int depthTexture = -1;
    private int stencilTexture = -1;
    private int stencilView = -1;
    private float[] framebufferColor = new float[4];

    private int width;
    private int height;

    public TextureFramebuffer(int width, int height)
    {
        createFrameBuffer(width, height);
        this.clear();
    }

    public void createFrameBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;
        framebufferColor[0] = 0.0f;
        framebufferColor[1] = 0.0f;
        framebufferColor[2] = 0.0f;
        framebufferColor[3] = 0.0f;
        framebuffer = OpenGlHelper.glGenFramebuffers();
        framebufferTexture = TextureUtil.glGenTextures();
        depthStencilTexture = TextureUtil.glGenTextures();
        depthTexture = TextureUtil.glGenTextures();
        stencilTexture = TextureUtil.glGenTextures();

        glBindTexture(GL_TEXTURE_2D, depthStencilTexture);
        // glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (IntBuffer) null);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (IntBuffer) null);
        // GL42.glTexStorage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (IntBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindTexture(GL_TEXTURE_2D, stencilTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_STENCIL_INDEX8, width, height, 0, GL_STENCIL_INDEX, GL_UNSIGNED_BYTE, (IntBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        GlStateManager.bindTexture(framebufferTexture);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebuffer);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, framebufferTexture, 0);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, stencilTexture, 0);
        // OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilTexture, 0);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) System.out.println("Error creating framebuffer: " + glCheckFramebufferStatus(GL_FRAMEBUFFER));
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
        GlStateManager.bindTexture(0);


        // GL13.glActiveTexture(GL13.GL_TEXTURE0);
        /*glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebuffer);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, framebufferTexture, 0);
        glBindTexture(GL_TEXTURE_2D,0);*/

        // GlStateManager.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, null);
    }



    public void clear()
    {
        bind();
        GlStateManager.clearColor(framebufferColor[0], framebufferColor[1], framebufferColor[2], framebufferColor[3]);
        clearDepth(1.0D);
        clearStencil(0);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
    }

    public void clearDepth(double depth)
    {
        bind();
        GlStateManager.clearDepth(depth);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
    }

    public void clearStencil(int value)
    {
        bind();
        glClearStencil(value);
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
    }

    public void bind()
    {
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebuffer);
    }

    /**
     * This method will bind the depth component of the texture to GL_TEXTURE0 and the stencil component to GL_TEXTURE1
     */
    /*public void bindDepthStencilTextures()
    {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, depthTexture); // used to be depthStencilTexture
        // glTexParameteri(GL_TEXTURE_2D, ARBStencilTexturing.GL_DEPTH_STENCIL_TEXTURE_MODE, GL_DEPTH_COMPONENT);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, stencilTexture); // used to be stencilView
        // glTexParameteri(GL_TEXTURE_2D, ARBStencilTexturing.GL_DEPTH_STENCIL_TEXTURE_MODE, GL_STENCIL_INDEX);
    }*/

    public void deleteFramebuffer()
    {
        if (OpenGlHelper.isFramebufferEnabled())
        {
            GlStateManager.bindTexture(0);
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);

            if (depthStencilTexture > -1)
            {
                TextureUtil.deleteTexture(framebufferTexture);
                framebufferTexture = -1;
            }

            if (depthTexture > -1)
            {
                TextureUtil.deleteTexture(depthTexture);
                depthTexture = -1;
            }

            if (stencilTexture > -1)
            {
                TextureUtil.deleteTexture(stencilTexture);
                stencilTexture = -1;
            }

            if (framebufferTexture > -1)
            {
                TextureUtil.deleteTexture(framebufferTexture);
                framebufferTexture = -1;
            }

            if (framebuffer > -1)
            {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
                OpenGlHelper.glDeleteFramebuffers(framebuffer);
                framebuffer = -1;
            }
        }
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getTexture()
    {
        return framebufferTexture;
    }

    public int getFBO()
    {
        return framebuffer;
    }

}
