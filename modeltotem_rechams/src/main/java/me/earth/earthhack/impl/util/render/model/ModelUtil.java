package me.earth.earthhack.impl.util.render.model;

import jassimp.*;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.Vec2d;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.image.EfficientTexture;
import me.earth.earthhack.impl.util.render.image.ImageUtil;
import me.earth.earthhack.impl.util.render.model.animation.Animation;
import me.earth.earthhack.impl.util.render.model.animation.RiggedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

// TODO: add mesh animations to JAssimp
public class ModelUtil implements Globals
{

    private static final String NAME = "D:\\DownloadsHDD\\astolfo-obj\\astolfo.obj";
    private static final String NAME1 = "D:\\DownloadsHDD\\astolfo-in-a-hoodie\\source\\Astolfo\\Astolfo.fbx";
    private static final String NAME2 = "D:\\DownloadsHDD\\benette\\source\\Benette.glb";
    private static final String NAME3 = "D:\\DownloadsHDD\\benette(1)\\scene.gltf";
    private static final String NAME4 = "D:\\DownloadsHDD\\kizuna-ai\\source\\Kizunaai.fbx";
    private static final String ROOT = "D:\\DownloadsHDD\\kizuna-ai\\textures";
    public static final String ROOT1 = "D:\\DownloadsHDD\\genshin-impact-amber\\source\\Amber\\Amber";
    public static final String ROOT2 = "D:\\DownloadsHDD\\astolfo-in-a-hoodie(1)\\source\\Astolfo";
    public static final String NAME6 = "D:\\DownloadsHDD\\astolfo-in-a-hoodie(1)\\source\\Astolfo\\Astolfo.fbx";
    public static final String NAME5 = "D:\\DownloadsHDD\\genshin-impact-amber\\source\\Amber\\Amber\\Amber.fbx";
    public static final String NAME7 = "D:\\DownloadsHDD\\kawaiope\\source\\cali_+keyed2_8+1.blend";
    public static final String NAME10 = "D:\\DownloadsHDD\\kawaiope(1)\\scene.gltf";
    public static final String ROOT3 = "D:\\DownloadsHDD\\kawaiope(1)";
    public static final String ROOT6 = "D:\\DownloadsHDD\\kawaiope\\source";
    public static final String NAME11 = "D:\\DownloadsHDD\\kawaiope\\source\\calli.fbx";
    public static final String NAME8 = "D:\\DownloadsHDD\\51dc47334dee42b9bb8e53ee07aa8006\\source\\sexygirl.blend";
    public static final String ROOT4 = "D:\\DownloadsHDD\\51dc47334dee42b9bb8e53ee07aa8006";
    public static final String NAME9 = "D:\\DownloadsHDD\\rigged-anime-girl-2020-outfits-expressions\\source\\ANIME GIRLS 2020\\RIGGED\\RIGGED\\suit_girl_update_NEW.fbx";
    public static final String ROOT5 = "D:\\DownloadsHDD\\rigged-anime-girl-2020-outfits-expressions\\textures";

    public static final String NAME12 = "D:\\DownloadsHDD\\zero-two-fully-rigged\\source\\02_pose1.fbx";
    public static final String NAME13 = "D:\\DownloadsHDD\\02\\02noncel_T_rigged.FBX";
    public static final String ROOT7 = "D:\\DownloadsHDD\\zero-two-fully-rigged";

    public static final VertexFormatElement NORMAL_3F = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.NORMAL, 3);

    public static final VertexFormatElement BONE_4I = new VertexFormatElement(0, VertexFormatElement.EnumType.INT, VertexFormatElement.EnumUsage.GENERIC, 4);
    public static final VertexFormatElement WEIGHT_4F = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.GENERIC, 4);
    
    public static final VertexFormat POS_NORMAL_TEX = new VertexFormat();
    public static final VertexFormat POS_NORMAL_TEX_BONE_WEIGHT = new VertexFormat();
    public static final VertexFormat POS_NORMAL = new VertexFormat();

    public static final GlShader SHADER = GlShader.createShader("modelrender");
    public static final GlShader SHADER_BONELESS = GlShader.createShader("modelrenderboneless");

    private static Mesh modelMesh;
    private static IModel model;
    public static final List<Mesh> meshes = new ArrayList<>();

    static
    {
        POS_NORMAL_TEX.addElement(DefaultVertexFormats.POSITION_3F);
        POS_NORMAL_TEX.addElement(NORMAL_3F);
        POS_NORMAL_TEX.addElement(DefaultVertexFormats.TEX_2F);

        POS_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
        POS_NORMAL.addElement(NORMAL_3F);

        POS_NORMAL_TEX_BONE_WEIGHT.addElement(DefaultVertexFormats.POSITION_3F);
        POS_NORMAL_TEX_BONE_WEIGHT.addElement(NORMAL_3F);
        POS_NORMAL_TEX_BONE_WEIGHT.addElement(DefaultVertexFormats.TEX_2F);
        POS_NORMAL_TEX_BONE_WEIGHT.addElement(BONE_4I);
        POS_NORMAL_TEX_BONE_WEIGHT.addElement(WEIGHT_4F);

        Jassimp.setWrapperProvider(new AssimpJomlProvider()); // wraps jassimp types into joml types
    }

    /*static
    {
        System.out.println("Hello there my friend.");
        try
        {
            loadModel();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Uh oh.");
        }
    }*/

    // TODO: animation loading
    public static Animation[] loadAnimations(String animationPath, boolean debug) throws IOException
    {
        AiScene scene = Jassimp.importFile(animationPath, Collections.singleton(AiPostProcessSteps.TRIANGULATE));
        for (AiAnimation animation : scene.getAnimations())
        {
            if (debug)
            {
                System.out.println(animation);
            }
        }
        return null;
    }

    // TODO: automatic texture dir finding, main model parsing from scene
    // TODO: if lighting is ever implemented tangents and bitangents for proper normal mapping
    public static IModel loadModel(String model, String root, boolean debug) throws IOException
    {
        List<Mesh> meshList = new ArrayList<>();
        Set<AiPostProcessSteps> steps = new HashSet<>();
        steps.add(AiPostProcessSteps.TRIANGULATE);
        steps.add(AiPostProcessSteps.FIX_INFACING_NORMALS);
        // steps.add(AiPostProcessSteps.FLIP_UVS); // zero two has infacing uvs
        AiScene scene = Jassimp.importFile(model, steps);
        System.out.println(scene);
        String name1 = scene.getSceneRoot(new AssimpJomlProvider()).getName();
        for (AiMesh mesh : scene.getMeshes())
        {
            if (debug)
            {
                System.out.println("Mesh name: " + mesh.getName());
            }

            AiMaterial material = scene.getMaterials().get(mesh.getMaterialIndex());
            List<Texture> textures = new ArrayList<>();
            if (material.hasProperties(Collections.singleton(AiMaterial.PropertyKey.TEX_FILE)))
            {
                int numTextures = material.getNumTextures(AiTextureType.DIFFUSE);
                for (int i = 0; i < numTextures; i++)
                {
                    String name = material.getTextureFile(AiTextureType.DIFFUSE, i);
                    try
                    {
                        if (debug)
                        {
                            System.out.println("Texture name: " + name);
                        }
                        BufferedImage image = ImageUtil.createFlipped(ImageUtil.bufferedImageFromFile(new File(root + File.separator + name)));
                        String generatedString = UUID.randomUUID().toString().split("-")[0];
                        EfficientTexture texture = ImageUtil.cacheBufferedImage(image, "png", generatedString);
                        // int id = ImageUtil.loadImage(image);

                        textures.add(new Texture(texture.getGlTextureId(), AiTextureType.DIFFUSE, texture, image.getWidth(), image.getHeight()));
                    }
                    catch (IOException | NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            int numFaces = mesh.getNumFaces();
            List<Vertex> vertices = new ArrayList<>();

            // TODO: restructure for bone parsing (we don't really need to do this at all)
            for (int j = 0; j < numFaces; j++)
            {
                int numFacesIndices = mesh.getFaceNumIndices(j);
                for (int k = 0; k < numFacesIndices; k++)
                {
                    Vertex vertex;
                    int i = mesh.getFaceVertex(j, k);
                    float x = mesh.getPositionX(i);
                    float y = mesh.getPositionY(i);
                    float z = mesh.getPositionZ(i);
                    float normX = mesh.getNormalX(i);
                    float normY = mesh.getNormalX(i);
                    float normZ = mesh.getNormalX(i);
                    boolean flag = false;
                    float u = 0.0f;
                    float v = 0.0f;
                    if (mesh.hasTexCoords(0))
                    {
                        u = mesh.getTexCoordU(i, 0);
                        v = mesh.getTexCoordV(i, 0);
                    }
                    vertex = new Vertex(new Vec3d(x, y, z), new Vec3d(normX, normY, normZ), new Vec2d(u, v));
                    vertices.add(vertex);
                }
            }

            meshList.add(new Mesh(vertices, mesh.getIndexBuffer(), textures));
        }
        Mesh[] meshArray = new Mesh[meshList.size()];
        for (int i = 0; i < meshArray.length; i++)
        {
            meshArray[i] = meshList.get(i);
        }

        AiMesh[] meshes = new AiMesh[scene.getNumMeshes()];
        for (int i = 0; i < meshes.length; i++) {
            meshes[i] = scene.getMeshes().get(i);
        }
        boolean rigged = scene.getNumAnimations() > 0; // TODO: improve!

        /*Animation animation = new Animation(scene, model1);
        AnimationWrapper wrapper = new AnimationWrapper(animation);*/
        // return new Model(null, meshArray, name1);
        if (rigged)
        {
            return new RiggedModel(scene, meshes, meshArray, name1);
        }
        else
        {
            return new Model(null, meshArray, name1);
        }
    }

    public static Animation[] loadAnimations(AiScene scene)
    {
        int numAnimations = scene.getNumAnimations();
        Animation[] animations = new Animation[numAnimations];
        for (int i = 0; i < numAnimations; i++)
        {
            AiAnimation animation = scene.getAnimations().get(i);
            double ticks = animation.getDuration();
            double tps = animation.getTicksPerSecond();
            int nodeID = 0;
            // AiBone
            for (AiNodeAnim node : animation.getChannels())
            {
                String name = node.getNodeName();
                int id = nodeID++;
            }
        }
        return animations;
    }

    public static void loadModel() throws IOException
    {
        // me.earth.earthhack.impl.util.render.model.lwjgl.ModelUtil.load(NAME5, "", 0);
        Set<AiPostProcessSteps> steps = new HashSet<>();
        steps.add(AiPostProcessSteps.TRIANGULATE);
        steps.add(AiPostProcessSteps.FIX_INFACING_NORMALS);
        // steps.add(AiPostProcessSteps.FLIP_UVS);
        // steps.add(AiPostProcessSteps.TRANSFORM_UV_COORDS);
        AiScene scene = Jassimp.importFile(NAME13, steps);
        System.out.println(scene);
        String name1 = scene.getSceneRoot(new AssimpJomlProvider()).getName();
        for (AiMesh mesh : scene.getMeshes())
        {
            AiMaterial material = scene.getMaterials().get(mesh.getMaterialIndex());
            List<Texture> textures = new ArrayList<>();
            if (material.hasProperties(Collections.singleton(AiMaterial.PropertyKey.TEX_FILE)))
            {
                for (AiTextureType type : AiTextureType.values())
                {
                    try
                    {
                        int numTextures = material.getNumTextures(type); // this is somehow null sometimes ???
                        for (int i = 0; i < numTextures; i++)
                        {
                            String name = material.getTextureFile(type, i);
                            try
                            {
                                System.out.println("Texture name: " + name + ", Type: " + type.name());
                                BufferedImage image = ImageUtil.createFlipped(ImageUtil.bufferedImageFromFile(new File(ROOT7 + File.separator + name)));
                                String generatedString = UUID.randomUUID().toString().split("-")[0];
                                EfficientTexture texture = ImageUtil.cacheBufferedImage(image, "png", generatedString);
                                // int id = ImageUtil.loadImage(image);

                                System.out.println("Texture dimensions: Width: " + image.getWidth() + " Height: " + image.getHeight());
                                textures.add(new Texture(texture.getGlTextureId(), type, texture, image.getWidth(), image.getHeight()));
                                System.out.println("Texture ID: " + texture.getGlTextureId());
                            }
                            catch (IOException | NoSuchAlgorithmException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (NullPointerException ignored) {} // TODO: we cant be havin this mane.
                }
            }

            int numVertices = mesh.getNumVertices();
            int numFaces = mesh.getNumFaces();
            List<Vertex> vertices = new ArrayList<>();
            System.out.println("Mesh name: " + mesh.getName());
            // IntBuffer indexBuffer = mesh.getIndexBuffer();
            // FloatBuffer buffer = mesh.getPositionBuffer();
            // System.out.println("Faces: " + numFaces);
            for (int j = 0; j < numFaces; j++)
            {
                int numFacesIndices = mesh.getFaceNumIndices(j);
                // OBJModel.OBJBakedModel
                // System.out.println("Face: " + j + ", Indices: " + numFacesIndices);
                for (int k = 0; k < numFacesIndices; k++)
                {
                    Vertex vertex;
                    int i = mesh.getFaceVertex(j, k);
                    // System.out.println("Face: " + j + ", Index: " + k + ", Vertex:" + i);
                    float x = mesh.getPositionX(i);
                    float y = mesh.getPositionY(i);
                    float z = mesh.getPositionZ(i);
                    float normX = mesh.getNormalX(i);
                    float normY = mesh.getNormalX(i);
                    float normZ = mesh.getNormalX(i);

                    boolean flag = false;
                    float u = 0.0f;
                    float v = 0.0f;
                    if (mesh.hasTexCoords(0))
                    {
                        u = mesh.getTexCoordU(i, 0);
                        v = mesh.getTexCoordV(i, 0);
                    }
                    // Gui.drawModalRectWithCustomSizedTexture();
                    // System.out.println("Tex coords: U: " + u + ", V:" + v);
                    vertex = new Vertex(new Vec3d(x, y, z), new Vec3d(normX, normY, normZ), new Vec2d(u, v));
                    vertices.add(vertex);
                }
            }

            /*if (material.hasProperties(Collections.singleton(AiMaterial.PropertyKey.TEX_FILE)))
            {
                for (int i = 0; i < material.getNumTextures(AiTextureType.DIFFUSE); i++)
                {
                    System.out.println("Mesh: " + mesh.getName() + ", Material: " + material.getName() + ", Diffuse File: " + material.getTextureFile(AiTextureType.DIFFUSE, i));
                }
                for (int i = 0; i < material.getNumTextures(AiTextureType.NORMALS); i++)
                {
                    System.out.println("Mesh: " + mesh.getName() + ", Material: " + material.getName() + ", Normal File: " + material.getTextureFile(AiTextureType.NORMALS, i));
                }
            }*/
            //

            /*for (int i = 0; i < numVertices; i++)
            {
                Vertex vertex;
                float x = mesh.getPositionX(i);
                float y = mesh.getPositionY(i);
                float z = mesh.getPositionZ(i);
                // float normX = mesh.getNormalX(i);
                // float normY = mesh.getNormalY(i);
                // float normZ = mesh.getNormalZ(i);
                // float texX = mesh.getTexCoordU()
                vertex = new Vertex(new Vec3d(x, y, z));
                vertices.add(vertex);
            }*/



            meshes.add(new Mesh(vertices, null, textures));
        }
        Mesh[] meshes1 = new Mesh[meshes.size()];
        meshes.toArray(meshes1);
        model = new Model(null, meshes1, name1);
    }

    // TODO: indexed rendering, skeletal animations
    public static void renderModel(Mesh[] meshArray, double x, double y, double z)
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

        GL11.glTranslated(x, y, z);
        for (Mesh mesh : meshArray)
        {
            // TODO: if we ever implement some form of lighting we need to bind normal textures as well
            for (int i = 0; i < mesh.getTextures().size(); i++)
            {
                Texture texture = mesh.getTextures().get(i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
                break;
            }

            mesh.getBuffer().bindBuffer();

            GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

            mesh.getBuffer().drawArrays(GL11.GL_TRIANGLES);
            mesh.getBuffer().unbindBuffer();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        GL11.glTranslated(-x, -y, -z);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderModel(double x, double y, double z)
    {
        model.render(x, y, z, mc.getRenderPartialTicks());
    }

    public static void renderModel1(double x, double y, double z)
    {
        // RenderUtil.startRender();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glTranslated(x, y, z);
        // GL11.glTranslated(x, y, z);
        for (Mesh mesh : meshes)
        {
            /*mesh.getBuffer().bindBuffer();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
            mesh.getBuffer().drawArrays(GL11.GL_TRIANGLES);
            mesh.getBuffer().unbindBuffer();
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);*/ // WORKS ONLY FOR POSITIONS, WE NEED A VERTEX ARRAY OBJECT FOR NORMAL AND TEX COORDS

            SHADER_BONELESS.bind();


            for (int i = 0; i < mesh.getTextures().size(); i++)
            {
                Texture texture = mesh.getTextures().get(i);
                // GL13.glActiveTexture(GL13.GL_TEXTURE0 + i); // Gl13.GL_TEXTURE0 and TEXTURE_1 are occupied by minecraft
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
                break;
                // GlStateManager.bindTexture(texture.getId());
            }

            SHADER_BONELESS.set("sampler", 0);

            // SHADER.set("model", RenderUtil.modelView);
            // SHADER.set("projection", RenderUtil.projection);


            // GL13.glActiveTexture(GL13.GL_TEXTURE0);

            // SHADER.bind();
            // SHADER.set("sampler", 0);

            mesh.getBuffer().bindBuffer();
            GL30.glBindVertexArray(mesh.getVAO());

            /*GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/


            /*GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);*/

            // GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0); no longer needed because of VAO
            mesh.getBuffer().drawArrays(GL11.GL_TRIANGLES);
            mesh.getBuffer().unbindBuffer();
            /*GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/
            GL30.glBindVertexArray(0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            /*GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/

            SHADER_BONELESS.unbind();

            // SHADER.unbind();

        }
        GL11.glTranslated(-x, -y, -z);
        // GL11.glTranslated(-x, -y, -z);
        // RenderUtil.endRender();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}