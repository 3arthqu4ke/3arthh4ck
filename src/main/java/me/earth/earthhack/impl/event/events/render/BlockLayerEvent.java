package me.earth.earthhack.impl.event.events.render;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

public class BlockLayerEvent
{
    private BlockRenderLayer layer = null;
    private final Block block;

    public BlockLayerEvent(Block block)
    {
        this.block = block;
    }

    public void setLayer(BlockRenderLayer layer)
    {
        this.layer = layer;
    }

    public BlockRenderLayer getLayer()
    {
        return this.layer;
    }

    public Block getBlock()
    {
        return block;
    }

}
