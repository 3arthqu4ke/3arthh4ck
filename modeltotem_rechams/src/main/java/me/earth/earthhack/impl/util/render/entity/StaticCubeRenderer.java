package me.earth.earthhack.impl.util.render.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Used when the color of a cube will not be changing frequently. Uses VBOs.
 * @author megyn
 */
public class StaticCubeRenderer
{

    private VertexBuffer buffer;
    // private boolean compiled;
    // private int list;
    private int mode;
    private float x1;
    private float y1;
    private float z1;
    private float x2;
    private float y2;
    private float z2;

    public StaticCubeRenderer(ByteBuffer vertices, VertexFormat format, int mode)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        if (OpenGlHelper.useVbo()) // this class should only ever be used when vbos are enabled, this check is redundant
        {
            buffer = new VertexBuffer(format);
            buffer.bindBuffer();
            buffer.bufferData(vertices);
            buffer.unbindBuffer();
        }
        this.mode = mode;
    }

    public void render(double partialTicks)
    {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        buffer.drawArrays(mode);
        glPopAttrib();
    }

}
