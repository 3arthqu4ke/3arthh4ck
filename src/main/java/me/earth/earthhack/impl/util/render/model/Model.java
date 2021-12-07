package me.earth.earthhack.impl.util.render.model;

import jassimp.AiMesh;
import jassimp.AiScene;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class Model implements IModel
{

    private String name;
    protected final Mesh[] meshes;
    protected final AiMesh[] aiMeshes;

    public Model(AiMesh[] meshes, Mesh[] meshes1, String name) {
        this.aiMeshes = meshes;
        this.meshes = meshes1;
        this.name = name;
        setupMeshes();
    }

    @Override
    public Mesh[] getMeshes()
    {
        return meshes;
    }

    @Override
    public void setupMesh(Mesh mesh)
    {
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(GL11.GL_TRIANGLES, ModelUtil.POS_NORMAL_TEX);
        for (Vertex vertex : mesh.getVertices())
        {
            builder
                    .pos(vertex.getPosition().x, vertex.getPosition().y, vertex.getPosition().z)
                    .normal((float) vertex.getNormal().x, (float) vertex.getNormal().y, (float) vertex.getNormal().z)
                    .tex(vertex.getTex().getX(), vertex.getTex().getY()) // Below this is optional, will later be abstracted out, just here for testing purposes.
                    .endVertex();
        }
        builder.finishDrawing();
        builder.reset();
        ByteBuffer modelBuffer = builder.getByteBuffer();

        mesh.getBuffer().bufferData(modelBuffer);


        mesh.setVAO(GL30.glGenVertexArrays());
        mesh.setEBO(OpenGlHelper.glGenBuffers());

        // TODO: update with
        mesh.getBuffer().bindBuffer();
        /*OpenGlHelper.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getEBO());
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndices(), GL15.GL_STATIC_DRAW);*/
        GL30.glBindVertexArray(mesh.getVAO());
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 32, 0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);
        /*GL20.glEnableVertexAttribArray(3);
        GL20.glVertexAttribPointer(3, 4, GL11.GL_INT, false, 64, 32);
        GL20.glEnableVertexAttribArray(4);
        GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 64, 48);*/
        GL30.glBindVertexArray(0);
        mesh.getBuffer().unbindBuffer();
    }

    @Override
    public Mesh[] genMeshes(AiScene scene) {
        return new Mesh[0];
    }

    @Override
    public void render(double x, double y, double z, double partialTicks)
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_TEXTURE_2D); // changed from enable
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE); // culling breaks some models' eyes
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        // GL11.glDepthMask(true);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glTranslated(x, y, z);
        // TODO: maybe vao + shader here too?
        ModelUtil.SHADER_BONELESS.bind();
        for (Mesh mesh : meshes)
        {
            // TODO: if we ever implement some form of lighting we need to bind normal textures as well
            for (int i = 0; i < mesh.getTextures().size(); i++)
            {
                Texture texture = mesh.getTextures().get(i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
                break;
            }

            ModelUtil.SHADER_BONELESS.set("sampler", 0);
            GL30.glBindVertexArray(mesh.getVAO());
            mesh.getBuffer().bindBuffer();

           /* GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/

            mesh.getBuffer().drawArrays(GL11.GL_TRIANGLES);
            mesh.getBuffer().unbindBuffer();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            // GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices());

            /*GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/
            GL30.glBindVertexArray(0);
        }
        ModelUtil.SHADER_BONELESS.unbind();
        GL11.glTranslated(-x, -y, -z);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
