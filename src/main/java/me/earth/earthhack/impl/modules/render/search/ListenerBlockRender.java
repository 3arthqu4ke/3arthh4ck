package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.render.BlockRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerBlockRender extends ModuleListener<Search, BlockRenderEvent>
{
    public ListenerBlockRender(Search module)
    {
        super(module, BlockRenderEvent.class);
    }

    @Override
    public void invoke(BlockRenderEvent event)
    {
        if (module.toRender.size() >= 100000)
        {
            module.toRender.clear();
        }

        BlockPos mut = event.getPos();
        Block block = event.getState().getBlock();
        if (mc.player.getDistanceSq(mut) <= 65536
                && block != Blocks.AIR
                && module.isValid(block.getLocalizedName()))
        {
            BlockPos pos = mut.toImmutable();
            IBlockState state = event.getState();

            AxisAlignedBB bb = state.getBoundingBox(mc.world, pos)
                                    .offset(pos.getX(), pos.getY(), pos.getZ());

            int stateColor = module.getColor(state);
            float r = (float) (stateColor >> 24 & 255) / 255.0f;
            float g = (float) (stateColor >> 16 & 255) / 255.0f;
            float b = (float) (stateColor >> 8 & 255) / 255.0f;
            float a = (float) (stateColor & 255) / 255.0f;

            module.toRender.put(pos, new SearchResult(pos, bb, r, g, b, a));
        }
    }

}
