package me.earth.earthhack.impl.util.render.entity;

import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a group of CubeRenderers.
 * @author megyn
 */
public class CustomModelRenderer
{

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public boolean isHidden;
    public boolean showModel = true;

    private List<BoxRenderer> renderers = new ArrayList<>();
    private List<CustomModelRenderer> children = new ArrayList<>();

    // TODO: replace calls to callList with my own rendering method

    public void renderWithScale(float scale)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
                    {
                        // GlStateManager.callList(this.displayList);
                        doRender(scale);
                        for (CustomModelRenderer child : children)
                        {
                            child.renderWithScale(scale);
                        }
                    }
                    else
                    {
                        GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                        // GlStateManager.callList(this.displayList);
                        doRender(scale);

                        for (CustomModelRenderer child : children)
                        {
                            child.renderWithScale(scale);
                        }

                        GlStateManager.translate(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);
                    }
                }
                else
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                    }

                    // GlStateManager.callList(this.displayList);
                    doRender(scale);

                    for (CustomModelRenderer child : children)
                    {
                        child.renderWithScale(scale);
                    }

                    GlStateManager.popMatrix();
                }

                GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
            }
        }
    }

    public void renderWithRotation(float scale)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleY != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F)
                {
                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                // GlStateManager.callList(this.displayList);
                doRender(scale);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    public void postRender(float scale)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                /*if (!this.compiled)
                {
                    this.compileDisplayList(scale);
                }*/

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
                {
                    if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F)
                    {
                        GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                    }
                }
                else
                {
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                    if (this.rotateAngleZ != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F)
                    {
                        GlStateManager.rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                    }
                }
            }
        }
    }

    public void doRender(float scale)
    {
        for (BoxRenderer renderer : renderers)
        {
            renderer.render(scale);
        }
    }

    public void addBox(float offX, float offY, float offZ, float width, float height, float depth, float delta)
    {

    }

}
