package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class ListenerUpdate extends ModuleListener<FastEat, MotionUpdateEvent>
{
    public ListenerUpdate(FastEat module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE
                && module.mode.getValue() == FastEatMode.Update
                && module.isValid(mc.player.getActiveItemStack()))
        {
            EnumHand hand = mc.player.getActiveHand();
            //noinspection ConstantConditions
            if (hand == null) // this can happen!
            {
                hand = mc.player.getHeldItemOffhand()
                                .equals(mc.player.getActiveItemStack())
                        ? EnumHand.OFF_HAND
                        : EnumHand.MAIN_HAND;
            }

            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItem(hand));
        }
        else if (event.getStage() == Stage.POST
                && module.mode.getValue() == FastEatMode.Packet
                && module.isValid(mc.player.getActiveItemStack())
                && mc.player.getItemInUseMaxCount()
                    > module.speed.getValue() - 1
                && module.speed.getValue() < 25)
        {
            for (int i = 0; i < 32; i++)
            {
                PingBypass.sendToActualServer(
                        new CPacketPlayer(mc.player.onGround));
            }

            PingBypass.sendToActualServer(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN));

            mc.player.stopActiveHand();
        }
    }

}
