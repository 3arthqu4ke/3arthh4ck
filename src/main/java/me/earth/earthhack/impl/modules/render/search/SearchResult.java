package me.earth.earthhack.impl.modules.render.search;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class SearchResult
{
    private final BlockPos pos;
    private final AxisAlignedBB bb;
    private final Color color;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public SearchResult(BlockPos pos,
                        AxisAlignedBB bb,
                        float red,
                        float green,
                        float blue,
                        float alpha)
    {
        this.pos   = pos;
        this.bb    = bb;
        this.red   = red;
        this.green = green;
        this.blue  = blue;
        this.alpha = alpha;
        this.color = new Color(red, green, blue, alpha);
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public AxisAlignedBB getBb()
    {
        return bb;
    }

    public float getRed()
    {
        return red;
    }

    public float getGreen()
    {
        return green;
    }

    public float getBlue()
    {
        return blue;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public Color getColor()
    {
        return color;
    }
}
