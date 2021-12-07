package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerDigging extends
        ModuleListener<FastEat, PacketEvent.Send<CPacketPlayerDigging>>
{
    public ListenerDigging(FastEat module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event)
    {
        if (module.cancel.getValue()
                && mc.player.getActiveItemStack().getItem() instanceof ItemFood)
        {
            CPacketPlayerDigging packet = event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM
                && packet.getFacing() == EnumFacing.DOWN
                && packet.getPosition().equals(BlockPos.ORIGIN))
            {
                event.setCancelled(true);
            }
        }
    }

}
