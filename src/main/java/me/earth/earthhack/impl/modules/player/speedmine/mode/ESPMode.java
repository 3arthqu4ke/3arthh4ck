package me.earth.earthhack.impl.modules.player.speedmine.mode;

import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public enum ESPMode
{
    None()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                /* None means no ESP. */
            }
        },
    Outline()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                RenderUtil.startRender();
                float red   = 255 - 255 * damage;
                float green = 255 * damage;
                RenderUtil.drawOutline(bb, 1.5F, new Color((int) red, (int) green, 0, module.getOutlineAlpha()));
                RenderUtil.endRender();
            }
        },
    Block()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                RenderUtil.startRender();
                float red   = 255 - 255 * damage;
                float green = 255 * damage;
                RenderUtil.drawBox(bb, new Color((int) red, (int) green, 0, module.getBlockAlpha()));
                RenderUtil.endRender();
            }
        },
    Box()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                Outline.drawEsp(module, bb, damage);
                Block.drawEsp(module, bb, damage);
            }
        };

    public abstract void drawEsp(Speedmine module, AxisAlignedBB bb, float damage);

}
