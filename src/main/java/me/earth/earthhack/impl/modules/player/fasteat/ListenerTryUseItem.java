package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerTryUseItem extends
        ModuleListener<FastEat, PacketEvent.Send<CPacketPlayerTryUseItem>>
{
    public ListenerTryUseItem(FastEat module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event)
    {
        if (module.mode.getValue() == FastEatMode.Update
                && module.isValid(mc.player.getHeldItem(event.getPacket()
                                                             .getHand())))
        {
            // no need to authorize it's NoEvent
            NetworkUtil.sendPacketNoEvent(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN));
        }
    }

}
