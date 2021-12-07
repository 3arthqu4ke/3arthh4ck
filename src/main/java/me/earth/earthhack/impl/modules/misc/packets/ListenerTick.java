package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<Packets, TickEvent>
{
    public ListenerTick(Packets module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe())
        {
            module.stateMap.clear();
            return;
        }

        if (!module.crashing.get()
                && module.bookCrash.getValue() != BookCrashMode.None)
        {
            module.startCrash();
        }

        for (int i = 0; i < module.offhandCrashes.getValue(); i++)
        {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.SWAP_HELD_ITEMS,
                    BlockPos.ORIGIN,
                    EnumFacing.UP));
        }
    }

}