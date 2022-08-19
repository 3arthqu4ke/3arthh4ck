package me.earth.earthhack.impl.managers.render;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author megyn
 * Let's not make so many framebuffers!
 * TODO: depth
 */
public class FrameBufferManager extends SubscriberImpl implements Globals
{

    private Framebuffer blockBuffer;

    protected int lastScale;
    protected int lastScaleWidth;
    protected int lastScaleHeight;

    public FrameBufferManager()
    {
        // ALL ESPS DRAWN WITH A FRAME BUFFER SHOULD HAVE THE LOWEST PRIORITY AND RESET THE MODELVIEW MATRIX!!!!!!! for block esps the blockBuffer should always be the one rendered to.
        this.listeners.add(new EventListener<Render3DEvent>(Render3DEvent.class, Integer.MIN_VALUE)
        {
            @Override
            public void invoke(Render3DEvent event)
            {
                checkSetupFBO();
            }
        });
        this.listeners.add(new EventListener<Render3DEvent>(Render3DEvent.class, Integer.MAX_VALUE)
        {
            @Override
            public void invoke(Render3DEvent event)
            {
                /*glPushMatrix();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glEnable(GL_TEXTURE_2D);
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
                FloatBuffer modelViewBuffer = GLAllocation.createDirectFloatBuffer(16);
                glGetFloat(GL_PROJECTION_MATRIX, buffer);
                glGetFloat(GL_MODELVIEW_MATRIX, modelViewBuffer);
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -2000.0f);

                drawFramebuffer(blockBuffer);

                glBindTexture(GL_TEXTURE_2D, 0);
                glDisable(GL_TEXTURE_2D);
                glMatrixMode(GL_PROJECTION);
                glLoadMatrix(buffer);
                glMatrixMode(GL_MODELVIEW);
                glLoadMatrix(modelViewBuffer);
                glPopAttrib();
                glPopMatrix();*/
            }
        });
    }

    private void checkSetupFBO()
    {
        final ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || blockBuffer == null) {
            blockBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true); // we will (maybe) be using depth later for some block esps/shaders, but for now this is fine.
            blockBuffer.framebufferClear();
        }
        else
        {
            blockBuffer.framebufferClear();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
    }

    public void drawFramebuffer(final Framebuffer framebuffer)
    {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        int currentProgram = glGetInteger(GL_CURRENT_PROGRAM);
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
        glUseProgram(currentProgram); // glUseProgram(0); should only be used when we are 100% sure that there will be no other active shader.
    }

    public Framebuffer getBlockBuffer()
    {
        return blockBuffer;
    }

    public void renderToBlockBuffer(float partialTicks, Runnable runnable)
    {
        blockBuffer.bindFramebuffer(true);
        ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(partialTicks, 0);
        runnable.run(); // just do whatever.
        mc.getFramebuffer().bindFramebuffer(true);
    }

}
