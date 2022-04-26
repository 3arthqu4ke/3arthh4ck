package me.earth.earthhack.impl.util.render.mutables;

import me.earth.earthhack.impl.util.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BBRender {
    public static void renderBox(BB bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        RenderUtil.startRender();
        drawOutline(bb, lineWidth, outLineColor);
        RenderUtil.endRender();
        RenderUtil.startRender();
        drawBox(bb, color);
        RenderUtil.endRender();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    // TODO: is it possible to improve on the performance of this function?
    public static void renderBox(BB bb,
                                 Color color,
                                 float lineWidth)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        RenderUtil.startRender();
        drawOutline(bb, lineWidth, color);
        RenderUtil.endRender();
        RenderUtil.startRender();
        drawBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.29f);
        RenderUtil.endRender();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void drawOutline(BB bb, float lineWidth, Color color) {
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

    public static void drawBox(BB bb, Color color) {
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

    public static void drawBox(BB bb, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        RenderUtil.color(red, green, blue, alpha);
        fillBox(bb);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void color(Color color) {
        GL11.glColor4f(color.getRed() / 255.0f,
                       color.getGreen() / 255.0f,
                       color.getBlue() / 255.0f,
                       color.getAlpha() / 255.0f);
    }

    public static void drawBox(BB bb) {
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

    public static void fillBox(BB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            GL11.glVertex3d((float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glVertex3d((float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            GL11.glEnd();
        }
    }

    public static void fillOutline(BB bb) {
        if (bb != null) {
            GL11.glBegin(GL11.GL_LINES);
            {
                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMinZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMinZ());

                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMinZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMinZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMinY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMinX(), bb.getMinY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMinZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMinZ());
                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMaxZ());

                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMaxZ());
                GL11.glVertex3d(bb.getMinX(), bb.getMaxY(), bb.getMinZ());
            }
            GL11.glEnd();
        }
    }
}
