package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
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
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            if (mc.world.getWorldBorder().contains(pos) && (!SPEED_MINE.isEnabled() || !pos.equals(SPEED_MINE.get().getPos())))
            {
                IBlockState state = mc.world.getBlockState(pos);
                if (state.getMaterial() != Material.AIR)
                {
                    // we should use MutableBB, too lazy, maybe if we write a 1.19 hack...
                    AxisAlignedBB bb = state.getSelectedBoundingBox(mc.world, pos);
                    if (!bb.equals(module.currentBB))
                    {
                        module.slideBB = module.currentBB;
                        module.currentBB = bb;
                        module.slideTimer.reset();
                    }

                    double factor;
                    AxisAlignedBB slide;
                    if (module.slide.getValue()
                        && (slide = module.slideBB) != null
                        && (factor = module.slideTimer.getTime() / Math.max(1.0, module.slideTime.getValue())) < 1.0)
                    {
                        AxisAlignedBB renderBB = new AxisAlignedBB(
                            slide.minX + (bb.minX - slide.minX) * factor,
                            slide.minY + (bb.minY - slide.minY) * factor,
                            slide.minZ + (bb.minZ - slide.minZ) * factor,
                            slide.maxX + (bb.maxX - slide.maxX) * factor,
                            slide.maxY + (bb.maxY - slide.maxY) * factor,
                            slide.maxZ + (bb.maxZ - slide.maxZ) * factor
                        );

                        module.renderInterpAxis(Interpolation.interpolateAxis(renderBB).grow(0.002));
                    }
                    else
                    {
                        module.renderInterpAxis(Interpolation.interpolateAxis(bb).grow(0.002));
                    }
                }
            }
        }
    }

}
