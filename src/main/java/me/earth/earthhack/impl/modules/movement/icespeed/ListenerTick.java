package me.earth.earthhack.impl.modules.movement.icespeed;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;

final class ListenerTick extends ModuleListener<IceSpeed, TickEvent>
{
    public ListenerTick(IceSpeed module)
    {
        super(module, TickEvent.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void invoke(TickEvent event)
    {
        Blocks.ICE.slipperiness         = module.speed.getValue();
        Blocks.PACKED_ICE.slipperiness  = module.speed.getValue();
        Blocks.FROSTED_ICE.slipperiness = module.speed.getValue();
    }

}
