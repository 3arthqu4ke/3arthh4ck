package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Vec2d;
import me.earth.earthhack.impl.util.misc.MutableBoundingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

// TODO: One Mutable.BlockPos for the MainThread
// TODO: One Mutable AxisAlignedBB for the MainThread
// TODO: One Frustum for the MainThread
//  That way we don't need to instantiate neither of them at all
//  Which could actually save quite the cost, since we render often
@SuppressWarnings("Duplicates")
public class RenderUtil implements Globals
{
    private static ScaledResolution res;

    private final static GlShader IMAGE_SHADER = GlShader.createShader("image");
    public final static GlShader BLUR_SHADER = GlShader.createShader("blur");

    // TODO: mutable axis aligned bb
    private static final BlockPos.MutableBlockPos RENDER_BLOCK_POS = new BlockPos.MutableBlockPos();
    private static final MutableBoundingBox RENDER_BOUNDING_BOX = new MutableBoundingBox();

    private static final VertexBuffer BLOCK_FILL_BUFFER = new VertexBuffer(DefaultVertexFormats.POSITION);
    private static final VertexBuffer BLOCK_OUTLINE_BUFFER = new VertexBuffer(DefaultVertexFormats.POSITION);

    public final static FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    public final static IntBuffer viewport = BufferUtils.createIntBuffer(16);
    public final static FloatBuffer viewportFloat = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer projection = BufferUtils.createFloatBuffer(16);

    static
    {
        res = new ScaledResolution(mc);
        genOpenGlBuffers();
    }

    public static void updateMatrices()
    {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        // GL11.glGetFloat(GL_VIEWPORT, viewportFloat);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GLUProjection.getInstance().updateMatrices(viewport, modelView, projection, (float) res.getScaledWidth() / (float) Minecraft.getMinecraft().displayWidth, (float) res.getScaledHeight() / (float) Minecraft.getMinecraft().displayHeight);
    }

    public static Entity getEntity()
    {
        return mc.getRenderViewEntity() == null
                    ? mc.player
                    : mc.getRenderViewEntity();
    }

    // TODO: perhaps programmatically gen vbos when settings in block esp modules change to support gradient rendering and differed boxes?
    // TODO: vbos for planes + streamline code for planes
    public static void genOpenGlBuffers()
    {
        if (OpenGlHelper.vboSupported)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1); // one block
            // filled box
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            // contains all position data for drawing a filled cube
            bufferBuilder.finishDrawing();
            bufferBuilder.reset();
            ByteBuffer byteBuffer = bufferBuilder.getByteBuffer();
            // BLOCK_FILL_BUFFER.bindBuffer();
            BLOCK_FILL_BUFFER.bufferData(byteBuffer);
            // BLOCK_FILL_BUFFER.unbindBuffer();

            bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.finishDrawing();
            bufferBuilder.reset();
            ByteBuffer outlineBuffer = bufferBuilder.getByteBuffer();
            // BLOCK_OUTLINE_BUFFER.bindBuffer();
            BLOCK_OUTLINE_BUFFER.bufferData(outlineBuffer);
            // BLOCK_OUTLINE_BUFFER.unbindBuffer();

        }
        else
        {
            Earthhack.getLogger().info("VBOs not supported, skipping.");
        }
    }

    public static void renderBox(double x, double y, double z)
    {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        double viewX = ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double viewY = ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double viewZ = ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        endRender();
    }

    public static void startRenderBox()
    {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
    }

    public static void endRenderBox()
    {
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        endRender();
    }

    public static void doRenderBox(double x, double y, double z)
    {
        double viewX = ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double viewY = ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double viewZ = ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
    }

    public static void renderBoxes(Vec3d[] vectors)
    {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        double viewX = ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double viewY = ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double viewZ = ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        for (Vec3d vec : vectors)
        {
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
            GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        }
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        endRender();
    }

    public static void renderOutline(double x, double y, double z)
    {
        startRender();
        BLOCK_OUTLINE_BUFFER.bindBuffer();
        double viewX = ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double viewY = ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double viewZ = ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        BLOCK_OUTLINE_BUFFER.drawArrays(GL11.GL_QUADS);
        BLOCK_OUTLINE_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        endRender();
    }

    public static void renderBox(BlockPos pos, Color color, float height)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        startRender();
        drawBox(bb, boxColor);
        endRender();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(BlockPos pos,
                                 Color color,
                                 float height,
                                 int boxAlpha)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha);
        startRender();
        drawBox(bb, boxColor);
        endRender();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(AxisAlignedBB bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        startRender();
        drawOutline(bb, lineWidth, outLineColor);
        endRender();
        startRender();
        drawBox(bb, color);
        endRender();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        fillBox(bb);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb, Color color)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        color(color);
        fillBox(bb);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    public static void drawOutline(AxisAlignedBB bb, float lineWidth)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    public static void drawOutline(AxisAlignedBB bb, float lineWidth, Color color)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        color(color);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }


    public static void fillOutline(AxisAlignedBB bb)
    {
        if (bb != null)
        {
            GL11.glBegin(GL11.GL_LINES);
            {
                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            GL11.glEnd();
        }
    }

    public static void fillBox(AxisAlignedBB boundingBox)
    {
        if (boundingBox != null)
        {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glEnd();
        }
    }

    public static void prepare(float x,
                               float y,
                               float x1,
                               float y1,
                               float lineWidth,
                               int color,
                               int color1,
                               int color2)
    {
        startRender();
        prepare(x, y, x1, y1, color2, color1);
        color(color);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }

    // only for nametags, i will clean up the render utils one day, but i had to get rid of those unnecessary calls ASAP
    public static void drawRect(float x, float y, float x1, float y1, float lineWidth, int color, int color1)
    {
        color(color1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void prepare(float x,
                               float y,
                               float x1,
                               float y1,
                               float lineWidth,
                               int color,
                               int color1)
    {
        startRender();
        prepare(x, y, x1, y1, color);
        color(color1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color, int color1)
    {
        startRender();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        color(color);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        color(color1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color)
    {
        startRender();
        color(color);
        scissor(x, y, x1, y1);
        endRender();
    }

    public static void scissor(float x, float y, float x1, float y1)
    {
        res = new ScaledResolution(mc);
        int scale = res.getScaleFactor();
        GL11.glScissor((int) (x * scale),
                       (int) ((res.getScaledHeight() - y1) * scale),
                       (int)((x1 - x) * scale),
                       (int)((y1 - y) * scale));
    }

    public static void color(Color color)
    {
        GL11.glColor4f(color.getRed() / 255.0f,
                       color.getGreen() / 255.0f,
                       color.getBlue() / 255.0f,
                       color.getAlpha() / 255.0f);
    }

    public static void color(int color)
    {
        float[] color4f = ColorUtil.toArray(color);
        GL11.glColor4f(color4f[0], color4f[1], color4f[2], color4f[3]);
    }

    public static void color(float r, float g, float b, float a)
    {
        GL11.glColor4f(r, g, b, a);
    }

    public static void startRender()
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void endRender()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void doPosition(AxisAlignedBB bb)
    {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glEnd();
    }

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height)
    {
        return (mouseX >= x && mouseX <= (x + width)) && (mouseY >= y && mouseY <= (y + height));
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color)
    {
        drawNametag(text, interpolated, scale, color, true);
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color, boolean rectangle)
    {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;

        drawNametag(text, x, y, z, scale, color, rectangle);
    }

    @SuppressWarnings("unused")
    public static void drawNametag(String text, double x, double y, double z, double scale, int color)
    {
        drawNametag(text, x, y, z, scale, color, true);
    }

    // TODO: Alpha is disabled
    @SuppressWarnings("DuplicatedCode")
    public static void drawNametag(String text, double x, double y, double z, double scale, int color, boolean rectangle)
    {
        //double dist = MathHelper.sqrt(x * x + y * y + z * z);
        double dist = RenderUtil.getEntity().getDistance(
                x + mc.getRenderManager().viewerPosX,
                y + mc.getRenderManager().viewerPosY,
                z + mc.getRenderManager().viewerPosZ);

        int textWidth = Managers.TEXT.getStringWidth(text) / 2;
        double scaling = 0.0018 + scale * dist;

        if (dist <= 8.0)
        {
            scaling = 0.0245;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y + 0.4f, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        float xRot = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.rotate(mc.getRenderManager().playerViewX, xRot, 0.0f, 0.0f);
        GlStateManager.scale(-scaling, -scaling, scaling);
        GlStateManager.disableDepth();

        if (rectangle)
        {
            GlStateManager.enableBlend();
            RenderUtil.prepare(-textWidth - 1, -Managers.TEXT.getStringHeight(), textWidth + 2, 1.0F, 1.8F, 0x55000400, 0x33000000);
            GlStateManager.disableBlend();
        }

        GlStateManager.enableBlend();
        Managers.TEXT.drawStringWithShadow(text, -textWidth, -(mc.fontRenderer.FONT_HEIGHT - 1), color);
        GlStateManager.disableBlend();

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, 1500000.0f);
        GlStateManager.popMatrix();
    }

    public static void drawBlurredBlock(BlockPos pos)
    {
        assert BLUR_SHADER != null;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
        BLUR_SHADER.bind();
        BLUR_SHADER.set("sampler", 0);
        BLUR_SHADER.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fillBox(bb);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        BLUR_SHADER.unbind();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /*public static Vec3d to2D(double x, double y, double z)
    {
        // GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        // GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        // GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new Vec3d(screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2));
        }
        return null;
    } <- fuck this method */

    public static Vec2d to2D(double x, double y, double z)
    {
        GLUProjection.Projection projection =  GLUProjection.getInstance().project(x, y, z, GLUProjection.ClampMode.ORTHOGONAL, true);
        return new Vec2d(projection.getX(), projection.getY());
    }

    public static void blurBlock(AxisAlignedBB bb, float intensity, float blurWidth, float blurHeight)
    {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (Render2DUtil.lastScale != factor || Render2DUtil.lastScaleWidth != factor2 || Render2DUtil.lastScaleHeight != factor3 || Render2DUtil.buffer == null
                || Render2DUtil.blurShader == null) {
            Render2DUtil.initFboAndShader();
        }
        Render2DUtil.lastScale = factor;
        Render2DUtil.lastScaleWidth = factor2;
        Render2DUtil.lastScaleHeight = factor3;

        if (OpenGlHelper.isFramebufferEnabled())
        {
            Render2DUtil.buffer.framebufferClear();

            Render2DUtil.setShaderConfigs(intensity, blurWidth, blurHeight);
            Render2DUtil.buffer.bindFramebuffer(true);

            Render2DUtil.blurShader.render(mc.getRenderPartialTicks());

            mc.getFramebuffer().bindFramebuffer(true);
            checkSetupFBO();

            ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(mc.getRenderPartialTicks(), 0);
            glEnable(GL11.GL_STENCIL_TEST);
            glClearStencil(0);
            glStencilOp(GL_KEEP, GL_REPLACE, GL_REPLACE);
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
            fillBox(bb);
            glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
            glStencilMask(0x00);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO,
                    GL11.GL_ONE);
            Render2DUtil.buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef(factor, factor, 0);
            glDisable(GL11.GL_STENCIL_TEST);
        }
    }

    protected static void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }

}
