package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerRender extends ModuleListener<BlockHighlight, Render3DEvent>
{
    private static final ModuleCache<Speedmine> SPEED_MINE =
            Caches.getModule(Speedmine.class);

    public ListenerRender(BlockHighlight module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (mc.objectMouseOver != null)
        {
            if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = mc.objectMouseOver.getBlockPos();
                if (mc.world.getWorldBorder().contains(pos)
                        && (!SPEED_MINE.isEnabled()
                            || !pos.equals(SPEED_MINE.get().getPos())))
                {
                    IBlockState state = mc.world.getBlockState(pos);
                    if (state.getMaterial() != Material.AIR)
                    {
                        AxisAlignedBB bb =
                            Interpolation
                                .interpolateAxis(state
                                        .getSelectedBoundingBox(mc.world, pos)
                                        .grow(0.0020000000949949026));

                        module.renderInterpAxis(bb);
                    }
                }
            }
        }
    }

}
