package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.impl.util.math.Vector3f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * ONLY USE FLOATS FOR COORDINATES
 * @author megyn
 */
public class PlainQuad
{
    
    private final Vector3f[] vertices = new Vector3f[4];
    private final VertexFormat format;
    private FloatBuffer buffer;

    public PlainQuad(Vector3f f, Vector3f f1, Vector3f f2, Vector3f f3, VertexFormat format, FloatBuffer buffer)
    {
        vertices[0] = f;
        vertices[1] = f1;
        vertices[2] = f2;
        vertices[3] = f3;
        this.format = format;
        this.buffer = buffer;
    }

    public void render(BufferBuilder renderer, float scale)
    {
        renderer.begin(GL11.GL_QUADS, format);

        int bufferIndex = 0;// maybe use get()?
        for (int i = 0; i < 4; ++i)
        {
            for (VertexFormatElement element : format.getElements())
            {
                int size = element.getSize();
                switch (element.getUsage())
                {
                    case POSITION:
                        float x = size >= 1 ? buffer.get(bufferIndex) : 0.0f;
                        float y = size >= 2 ? buffer.get(bufferIndex + 1) : 0;
                        float z = size >= 3 ? buffer.get(bufferIndex + 2) : 0;
                        renderer.pos(x * scale, y * scale, z * scale);
                        break;
                    case UV:
                        float u = size >= 1 ? buffer.get(bufferIndex) : 0.0f;
                        float v = size >= 2 ? buffer.get(bufferIndex + 1) : 0.0f;
                        renderer.tex(u * scale, v * scale);
                        break;
                    case NORMAL:
                        float f = size >= 1 ? buffer.get(bufferIndex) : 0.0f;
                        float f1 = size >= 2 ? buffer.get(bufferIndex + 1) : 0.0f;
                        float f2 = size >= 3 ? buffer.get(bufferIndex + 2) : 0.0f;
                        renderer.normal(f * scale, f1 * scale, f2 * scale);
                        break;
                    case COLOR:
                        float r = size >= 1 ? buffer.get(bufferIndex) : 0.0f;
                        float g = size >= 2 ? buffer.get(bufferIndex + 1) : 0.0f;
                        float b = size >= 3 ? buffer.get(bufferIndex + 2) : 0.0f;
                        float a = size >= 4 ? buffer.get(bufferIndex + 3) : 1.0f;
                        renderer.color(r * scale, g * scale, b * scale, a * scale);
                        break;
                    default:
                }
                bufferIndex += size;
            }
            renderer.endVertex();
        }

        Tessellator.getInstance().draw();
    }

    public FloatBuffer getBuffer()
    {
        return buffer;
    }

    public void setBuffer(FloatBuffer buffer)
    {
        // this.buffer.clear();
        this.buffer = buffer;
    }

}
