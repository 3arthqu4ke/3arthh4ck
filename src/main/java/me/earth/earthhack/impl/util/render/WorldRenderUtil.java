package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Globals;

public class WorldRenderUtil implements Globals
{
    public static void reload(boolean soft)
    {
        if (soft)
        {
            int x = (int) mc.player.posX;
            int y = (int) mc.player.posY;
            int z = (int) mc.player.posZ;
            int d = mc.gameSettings.renderDistanceChunks * 16;
            mc.renderGlobal.markBlockRangeForRenderUpdate(
                    x - d, y - d, z - d, x + d, y + d, z + d);
            return;
        }

        mc.renderGlobal.loadRenderers();
    }

}
