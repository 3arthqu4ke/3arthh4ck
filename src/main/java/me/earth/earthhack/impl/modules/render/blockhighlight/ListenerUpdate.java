package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

final class ListenerUpdate extends ModuleListener<BlockHighlight, UpdateEvent>
{
    public ListenerUpdate(BlockHighlight module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void invoke(UpdateEvent event)
    {
        if (mc.objectMouseOver != null)
        {
            switch (mc.objectMouseOver.typeOfHit)
            {
                case BLOCK:
                    BlockPos pos = mc.objectMouseOver.getBlockPos();
                    if (mc.world.getWorldBorder().contains(pos))
                    {
                        IBlockState state = mc.world.getBlockState(pos);
                        if (state.getMaterial() != Material.AIR)
                        {
                            /* Forge method, doesn't work in vanilla

                            ItemStack stack = state.getBlock().getPickBlock(
                                                            state,
                                                            mc.objectMouseOver,
                                                            mc.world,
                                                            pos,
                                                            mc.player);
                           */

                            ItemStack stack = state
                                    .getBlock()
                                    .getItem(mc.world, pos, state);

                            module.current = stack
                                                .getItem()
                                                .getItemStackDisplayName(stack);
                            return;
                        }
                    }
                    break;
                case ENTITY:
                    Entity entity = mc.objectMouseOver.entityHit;
                    if (entity != null)
                    {
                        module.current = EntityNames.getName(entity);
                        return;
                    }
                    break;
                default:
            }
        }

        module.current = null;
    }

}
