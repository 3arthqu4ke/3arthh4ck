package me.earth.earthhack.impl.util.render.shader;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * Allows rendering to a framebuffer with more ease than with the normal Minecraft Framebuffer class.
 * @author megyn
 */
public class FramebufferWrapper implements Globals
{

    private Framebuffer framebuffer;
    protected static int lastScale;
    protected static int lastScaleWidth;
    protected static int lastScaleHeight;
    private boolean hasUpdated;

    public FramebufferWrapper()
    {
        updateFramebuffer();
    }

    /**
     * Clear framebuffer, check if scale has changed
     */
    public void updateFramebuffer()
    {
        hasUpdated = false;
        // If the display is not active or visible, this will loop and nuke performance.
        if (Display.isActive() || Display.isVisible())
        {
            if (framebuffer != null)
            {
                framebuffer.framebufferClear();
                ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
                int factor = scale.getScaleFactor();
                int factor2 = scale.getScaledWidth();
                int factor3 = scale.getScaledHeight();
                if (lastScale != factor
                        || lastScaleWidth != factor2
                        || lastScaleHeight != factor3)
                {
                    framebuffer.deleteFramebuffer();
                    framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                    hasUpdated = true;
                    // framebuffer.framebufferClear();
                }
                lastScale = factor;
                lastScaleWidth = factor2;
                lastScaleHeight = factor3;
            }
            else
            {
                framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                hasUpdated = true;
            }
        }
        else
        {
            if (framebuffer == null)
            {
                framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                hasUpdated = true;
            }
        }
    }

    /**
     * @param renderOp runnable containing OpenGL calls to be rendered on this framebuffer
     */
    public void renderToFramebuffer(SafeRunnable renderOp)
    {
        GlStateManager.pushAttrib();
        framebuffer.bindFramebuffer(true);
        renderOp.run();
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.popAttrib();
    }

    public void renderFramebuffer(SafeRunnable... renderOp)
    {
        GlStateManager.pushAttrib();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);

        // mc.entityRenderer.setupOverlayRendering();
        for (SafeRunnable runnable : renderOp) runnable.run();
        drawFramebuffer(framebuffer);

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popAttrib();
    }

    public void drawFramebuffer(final Framebuffer framebuffer)
    {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, 0);
        glTexCoord2d(0, 0);
        glVertex2d(0, scaledResolution.getScaledHeight());
        glTexCoord2d(1, 0);
        glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        glTexCoord2d(1, 1);
        glVertex2d(scaledResolution.getScaledWidth(), 0);
        glEnd();
    }

    public Framebuffer getFramebuffer()
    {
        return framebuffer;
    }

    public boolean hasUpdated()
    {
        return hasUpdated;
    }
}
