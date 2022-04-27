package me.earth.earthhack.impl.util.render.model.animation;

import jassimp.AiBone;
import jassimp.AiBoneWeight;
import jassimp.AiMesh;
import jassimp.AiScene;
import me.earth.earthhack.impl.util.render.model.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class RiggedModel implements IModel
{

    private String name;
    private final Map<String, BoneInfo> bones = new HashMap<>();
    private final AiMesh[] aiMeshes;
    private final Mesh[] meshes;
    private final Animation[] animations;
    private final AnimationWrapper[] animationWrappers;
    private final int currentAnimation;
    private int boneCounter = 0;
    private long lastFrame = -1;

    public RiggedModel(AiScene scene, AiMesh[] aiMeshes, Mesh[] meshes, String name)
    {
        this.aiMeshes = aiMeshes;
        this.meshes = meshes;
        this.name = name;
        this.setupMeshes();
        setMeshBones();
        this.animations = new Animation[scene.getNumAnimations()];
        for (int i = 0; i < animations.length; i++)
        {
            animations[i] = new Animation(scene, this, i);
        }
        animationWrappers = new AnimationWrapper[animations.length];
        for (int i = 0; i < animationWrappers.length; i++)
        {
            animationWrappers[i] = new AnimationWrapper(animations[i]);
        }
        this.currentAnimation = 0;
    }

    private void setMeshBones() {
        for (int k = 0; k < meshes.length; k++)
        {
            AiMesh mesh = aiMeshes[k];
            Mesh customMesh = meshes[k];
            for (int i = 0; i < mesh.getBones().size(); i++)
            {
                int boneID;
                AiBone bone = mesh.getBones().get(i);
                String name = bone.getName();
                System.out.println("Mesh name: " + mesh.getName() + " Bone: " + name);
                if (!bones.containsKey(name))
                {
                    BoneInfo info = new BoneInfo(boneCounter, bone.getOffsetMatrix(new AssimpJomlProvider()));
                    boneID = boneCounter;
                    bones.put(name, info);
                    boneCounter++;
                    if (boneCounter >= 100) break; // TODO
                }
                else
                {
                    boneID = bones.get(name).getId();
                }

                int numWeights = bone.getNumWeights();
                for (int j = 0; j < numWeights; j++)
                {
                    AiBoneWeight weight = bone.getBoneWeights().get(j);
                    int id = weight.getVertexId();
                    float fWeight = weight.getWeight();
                    Vertex vertex = customMesh.getVertices().get(id);
                    for (int l = 0; l < 4; l++)
                    {
                        if (vertex.getBones()[l] < 0)
                        {
                            vertex.getWeights()[l] = fWeight;
                            vertex.getBones()[l] = boneID;
                        }
                    }
                }
            }
        }
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
                    .color(vertex.getBones()[0], vertex.getBones()[1], vertex.getBones()[2], vertex.getBones()[3])
                    .color(vertex.getWeights()[0] / 255.0f, vertex.getWeights()[1] / 255.0f, vertex.getWeights()[2] / 255.0f, vertex.getWeights()[3] / 255.0f)
                    .endVertex();
        }
        builder.finishDrawing();
        builder.reset();
        ByteBuffer modelBuffer = builder.getByteBuffer();

        mesh.getBuffer().bufferData(modelBuffer);


        mesh.setVAO(GL30.glGenVertexArrays());

        // TODO: update with
        mesh.getBuffer().bindBuffer();
        GL30.glBindVertexArray(mesh.getVAO());
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
        mesh.getBuffer().unbindBuffer();
    }

    // TODO: maybe implement this?
    @Override
    public Mesh[] genMeshes(AiScene scene)
    {
        return new Mesh[0];
    }

    @Override
    public void render(double x, double y, double z, double partialTicks)
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (lastFrame == -1) lastFrame = System.currentTimeMillis();
        long current = System.currentTimeMillis();
        long delta = current - lastFrame;
        lastFrame = current;

        ModelUtil.SHADER.bind();
        AnimationWrapper wrapper = animationWrappers[currentAnimation];
        wrapper.update(delta);
        Matrix4f[] matrices = wrapper.getMatrices();
        for (int i = 0; i < matrices.length; i++)
        {
            ModelUtil.SHADER.set("finalBonesMatrices[" + i + "]", matrices[i]);
        }
        GL11.glTranslated(x, y, z);
        for (Mesh mesh : meshes)
        {

            // TODO: if we ever implement some form of lighting we need to bind normal textures as well
            for (int i = 0; i < mesh.getTextures().size(); i++)
            {
                Texture texture = mesh.getTextures().get(i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
                break;
            }

            ModelUtil.SHADER.set("sampler", 0);
            mesh.getBuffer().bindBuffer();
            GL30.glBindVertexArray(mesh.getVAO());
            /*GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/

            mesh.getBuffer().drawArrays(GL11.GL_TRIANGLES);
            mesh.getBuffer().unbindBuffer();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL30.glBindVertexArray(0);
            /*GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/
        }
        ModelUtil.SHADER.unbind();
        GL11.glTranslated(-x, -y, -z);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public Mesh[] getMeshes()
    {
        return meshes;
    }

    public Map<String, BoneInfo> getBones()
    {
        return bones;
    }

    public int getBoneCount()
    {
        return boneCounter;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public Animation[] getAnimations() {
        return animations;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
