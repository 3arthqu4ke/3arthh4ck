package me.earth.earthhack.impl.util.render.model;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: code cleanup and abstraction
 */
public class Mesh
{

    private final VertexBuffer meshBuffer;
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;
    private List<Vertex> vertices;
    // private List<Integer> indices;
    private IntBuffer indices;
    private List<Texture> textures;
    private boolean rigged;

    private int vbo, vao, ebo;

    public Mesh(List<Vertex> vertices, IntBuffer indices, List<Texture> textures)
    {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;
        this.meshBuffer = new VertexBuffer(ModelUtil.POS_NORMAL_TEX);
        // setupMesh();
    }

    public Mesh(List<Vertex> vertices, IntBuffer indices, List<Texture> textures, VertexFormat format, boolean rigged)
    {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;
        this.meshBuffer = new VertexBuffer(format);
        // setupMesh();
    }

    /*private void setupMesh()
    {
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(GL11.GL_TRIANGLES, ModelUtil.POS_NORMAL_TEX);
        for (Vertex vertex : vertices)
        {
            builder
                    .pos(vertex.getPosition().x, vertex.getPosition().y, vertex.getPosition().z)
                    .normal((float) vertex.getNormal().x, (float) vertex.getNormal().y, (float) vertex.getNormal().z)
                    .tex(vertex.getTex().getX(), vertex.getTex().getY()) // Below this is optional, will later be abstracted out, just here for testing purposes.
                    .color(vertex.getBones()[0], vertex.getBones()[1], vertex.getBones()[2], vertex.getBones()[3])
                    .color(vertex.getWeights()[0] / 255.0f, vertex.getWeights()[1] / 255.0f, vertex.getWeights()[2] / 255.0f, vertex.getWeights()[3] / 255.0f)
                    .endVertex();
        }
        builder.finishDrawing();
        builder.reset();
        ByteBuffer modelBuffer = builder.getByteBuffer();

        meshBuffer.bufferData(modelBuffer);


        vao = GL30.glGenVertexArrays();

        // TODO: update with
        meshBuffer.bindBuffer();
        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 64, 0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 64, 12);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 64, 24);
        GL20.glEnableVertexAttribArray(3);
        GL20.glVertexAttribPointer(3, 4, GL11.GL_INT, false, 64, 32);
        GL20.glEnableVertexAttribArray(4);
        GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 64, 48);
        GL30.glBindVertexArray(0);
        meshBuffer.unbindBuffer();
    }*/

    public VertexBuffer getBuffer()
    {
        return meshBuffer;
    }

    public int getVAO()
    {
        return vao;
    }

    public void setVAO(int vao)
    {
        this.vao = vao;
    }

    public int getEBO()
    {
        return ebo;
    }

    public void setEBO(int ebo)
    {
        this.ebo = ebo;
    }

    public List<Texture> getTextures()
    {
        return textures;
    }

    public List<Vertex> getVertices()
    {
        return vertices;
    }

    public IntBuffer getIndices()
    {
        return indices;
    }

    public List<Vector3f> getVerticesAsVector()
    {
        List<Vector3f> toReturn = new ArrayList<>();
        for (Vertex vertex : getVertices())
        {
            toReturn.add(new Vector3f((float) vertex.getPosition().x, (float) vertex.getPosition().y, (float) vertex.getPosition().z));
        }
        return toReturn;
    }

}
