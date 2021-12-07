package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import net.minecraft.init.Blocks;

final class ListenerBlockBreak extends
        ModuleListener<LegSwitch, BlockDestroyEvent>
{
    private final BlockStateHelper helper = new BlockStateHelper();

    public ListenerBlockBreak(LegSwitch module)
    {
        super(module, BlockDestroyEvent.class, 11);
    }

    @Override
    public void invoke(BlockDestroyEvent event)
    {
        if (!module.breakBlock.getValue()
            || event.isCancelled()
            || event.isUsed()
            || event.getStage() != Stage.PRE)
        {
            return;
        }

        event.setUsed(true);
        helper.addBlockState(event.getPos(), Blocks.AIR.getDefaultState());
        module.startCalculation(helper);
        helper.delete(event.getPos());
    }

}
