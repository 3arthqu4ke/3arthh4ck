package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerUseItem extends
        ModuleListener<FastPlace, PacketEvent.Send<CPacketPlayerTryUseItem>>
{
    private boolean sending;

    public ListenerUseItem(FastPlace module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event)
    {
        if (!module.doubleEat.getValue()
            || sending
            || event.isCancelled()
            || !(mc.player.getHeldItem(event.getPacket().getHand())
                          .getItem() instanceof ItemFood))
        {
            return;
        }

        sending = true;
        mc.player.connection.sendPacket(
                new CPacketPlayerTryUseItem(event.getPacket().getHand()));
        sending = false;
        PingBypass.sendToActualServer(
                new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN));
    }

}
